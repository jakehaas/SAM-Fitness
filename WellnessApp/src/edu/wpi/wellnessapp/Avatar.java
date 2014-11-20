package edu.wpi.wellnessapp;

public class Avatar
{

	private static Avatar avatar = new Avatar();
	

	private Avatar()
	{
		
	}

	/* Static 'instance' method */
	public static Avatar getInstance()
	{
		return avatar;
	}

	protected static void loadAvatar()
	{
		// TODO Auto-generated method stub

	}

}