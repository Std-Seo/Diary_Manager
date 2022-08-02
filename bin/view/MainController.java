package view;

import java.sql.*;
import java.time.LocalDate;

import domain.Todo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import util.AppUtil;
import util.DBconnect;

public class MainController {
	@FXML
	private DatePicker datePicker;
	@FXML
	private TextField txtName;
	
	@FXML
	private ListView<Todo> list;
	
	private ObservableList<Todo> items;
	
	private DBconnect db;
	
	@FXML
	private void initialize() {
		items = FXCollections.observableArrayList();
		list.setItems(items);
		db = new DBconnect();
		
		Connection conn = db.getConnection();
		if(conn == null) {
			AppUtil.alert("DB 연결 실패", null);
			return;
		}
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from todos";
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				int id = rs.getInt("id");
				String memo = rs.getString("memo");
				LocalDate date = rs.getDate("todate").toLocalDate();
				
				Todo todo = new Todo(id, memo, date);
				items.add(todo);
			}
		}
			catch(Exception e) {
				e.printStackTrace();
				AppUtil.alert("데이터 삽입 실패", null);
				return;
			}
			finally {
				if(rs != null) try {rs.close(); } catch(Exception e) {}
				if(stmt != null) try {stmt.close(); } catch(Exception e) {}
				if(conn != null) try {conn.close(); } catch(Exception e) {}
			}
		}
	
	public void addTodo() {
		String name = txtName.getText();
		
		if(name.isEmpty()) {
			AppUtil.alert("할 일의 이름을 입력하셔야 합니다.", null);
			return;
		}
		
		LocalDate date = datePicker.getValue();
		if(date == null) {
			AppUtil.alert("날짜를 입력하세요", null);
			return;
		}
		Connection conn = db.getConnection();
		if(conn == null) {
			AppUtil.alert("DB 연결 실패", null);
			return;
		}
		PreparedStatement pstmt = null;
		String sql = "insert into todos(id, memo, todate) values(todo_seq.nextval, ?, ?)";
		
		int insertId = 0;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setDate(2, Date.valueOf(date));
			int cnt = pstmt.executeUpdate();
			if(cnt == 0) {
				AppUtil.alert("데이터 삽입 실패", null);
				return;
			}
//			ResultSet key = pstmt.getGeneratedKeys();
//			if(key.next()) {
//				insertId = key.getInt(1);
//			}
		}
		catch(Exception e) {
			e.printStackTrace();
			AppUtil.alert("데이터 삽입 실패", null);
			return;
		}
		finally {
			if(pstmt != null) try { pstmt.close(); } catch(Exception e) {}
			if(conn != null) try { conn.close(); } catch(Exception e) {}
		}
		
		Todo todo = new Todo(insertId, name, date);
		items.add(todo);
	}
	
	public void delTodo() {
		int idx = list.getSelectionModel().getSelectedIndex();
		if(idx >= 0) {
			Todo todo = items.get(idx);

			Connection conn = db.getConnection();
			if(conn == null) {
				AppUtil.alert("DB연결에 실패했습니다.", null);
				return;
			}
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement("delete from todos where id = ?");
				pstmt.setInt(1, todo.getId());
				int cnt = pstmt.executeUpdate();
				
				if(cnt == 0) {
					AppUtil.alert("데이터 삭제 실패", null);
					return;
				}
			}
			catch(Exception e) {
				AppUtil.alert("데이터 삭제 실패", null);
				return;
			}
			finally {
				if(pstmt != null) try { pstmt.close(); } catch (Exception e) {}
				if(conn != null) try { conn.close(); } catch (Exception e) {}
			}
			items.remove(idx);
			
		} else {
			AppUtil.alert("삭제할 아이템을 선택하세요", "에러");
		}
	}
	

}
