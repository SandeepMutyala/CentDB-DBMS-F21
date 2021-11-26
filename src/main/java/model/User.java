package model;

import java.io.Serializable;

public class User implements Serializable {
		public User() {
			super();
		}
		
		private String user_id;
		private String username;
		private String password;
		private String security_answer1;
		private String security_answer2;
		
		
		public String getUser_id() {
			return user_id;
		}
		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getSecurity_answer1() {
			return security_answer1;
		}
		public void setSecurity_answer1(String security_answer1) {
			this.security_answer1 = security_answer1;
		}
		public String getSecurity_answer2() {
			return security_answer2;
		}
		public void setSecurity_answer2(String security_answer2) {
			this.security_answer2 = security_answer2;
		}
		
		@Override
		public String toString() {
			return "User [user_id=" + user_id + ", username=" + username + ", password=" + password
					+ ", security_answer1=" + security_answer1 + ", security_answer2=" + security_answer2 + "]";
		}
}
