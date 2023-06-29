package it.polito.tdp.nyc.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.nyc.model.Hotspot;
import it.polito.tdp.nyc.model.NTA;

public class NYCDao {
	
	public List<Hotspot> getAllHotspot(){
		String sql = "SELECT * FROM nyc_wifi_hotspot_locations";
		List<Hotspot> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Hotspot(res.getInt("OBJECTID"), res.getString("Borough"),
						res.getString("Type"), res.getString("Provider"), res.getString("Name"),
						res.getString("Location"),res.getDouble("Latitude"),res.getDouble("Longitude"),
						res.getString("Location_T"),res.getString("City"),res.getString("SSID"),
						res.getString("SourceID"),res.getInt("BoroCode"),res.getString("BoroName"),
						res.getString("NTACode"), res.getString("NTAName"), res.getInt("Postcode")));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}
	
	//metodo per ottenere solo i borough 
	public List<String> getBorough() {
		
		String sql = "SELECT DISTINCT Borough "
				+ "FROM nyc_wifi_hotspot_locations " 
				+ "ORDER BY Borough ASC ";
		
		List<String> result = new ArrayList<String>();
		
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				result.add(rs.getString("Borough"));
			}
			
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error");
			e.printStackTrace();
			return null;
		}
		return result;
		
	}
	
	
	//per estrarre i VERITICI E PESO del grafo  --> bisogna crerare una CLASSA A PARTE PER I VERTICI (NTA) E PESO 
	public List<NTA> getNTA(String b) {
		
		String sql = "SELECT DISTINCT NTACode, SSID "
				+ "FROM nyc_wifi_hotspot_locations "
				+ "WHERE Borough = ? "
				+ "ORDER BY NTACode ";
		
		List<NTA> result = new ArrayList<NTA>();
		
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, b);
			ResultSet rs = st.executeQuery();
			
			String lastNTACode = "";
			while(rs.next()) {
				if (!(rs.getString("NTACode")).equals(lastNTACode)) {
					Set<String > ssdi = new HashSet<>();
					ssdi.add(rs.getString("SSID"));
					result.add(new NTA(rs.getString("NTACode"), ssdi));
					lastNTACode = rs.getString("NTACode");
				}else {
					result.get(result.size()-1).getSSDI().add(rs.getString("SSID")) ;
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Errore");
			e.printStackTrace();
			return null;
		}
		return result;
		
	}
	
	
}
