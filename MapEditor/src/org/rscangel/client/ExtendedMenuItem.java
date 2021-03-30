package org.rscangel.client;

import java.awt.MenuItem;
import java.awt.MenuShortcut;

public class ExtendedMenuItem extends MenuItem
{
	private static final long serialVersionUID = 7526472295622776147L;
	private MenuEvent mID;

	public ExtendedMenuItem(String label)
	{
		super(label);
	}
	
	// -------------------------------------------------------------------------------------------------------------------
	public ExtendedMenuItem( String title, MenuShortcut shortcut )
	{
		super( title, shortcut );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setEventID( MenuEvent me)
	{
		mID = me;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public MenuEvent getEventID()
	{
		return mID;
	}
}
