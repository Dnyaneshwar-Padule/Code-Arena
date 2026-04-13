package util;

import service.impl.UserServiceImpl;

public class AddAdmin {

	public static void main(String[] args) {

		String adminEmail = "admin@email.com";
		String password = "@123admin";
		String userName = "Admin";
		new UserServiceImpl().createAdmin(userName, adminEmail, password);
	
	}

}
