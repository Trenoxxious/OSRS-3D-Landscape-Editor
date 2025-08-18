package org.rscangel.client;

import java.awt.*;

import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

//-------------------------------------------------------------------------------------------------------------------
public class GameFrame extends Frame implements ActionListener
{
	private static final long serialVersionUID = 7526472295622776147L;

	SelectSectorsFrame selectSectorsFrame = null;

	private MenuItem[] editModes = new MenuItem[2];
	private MenuItem roofViews;
	
	// -------------------------------------------------------------------------------------------------------------------
	MenuBar mainMenu = null;
	Menu fileMenu = null;

	mudclient client = null;

	// -------------------------------------------------------------------------------------------------------------------
	public void showSelectSectorsFrame()
	{
		if( selectSectorsFrame == null )
		{
			selectSectorsFrame = new SelectSectorsFrame( client );

			selectSectorsFrame.setVisible( true );
			selectSectorsFrame.setResizable( false );
			selectSectorsFrame.setAlwaysOnTop( true );
			selectSectorsFrame.setMudClient( client );
		}

		if( !selectSectorsFrame.isVisible() )
		{
			selectSectorsFrame = new SelectSectorsFrame( client );

			selectSectorsFrame.setVisible( true );
			selectSectorsFrame.setResizable( false );
			selectSectorsFrame.setAlwaysOnTop( true );
			selectSectorsFrame.setMudClient( client );
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public GameFrame( GameWindow gameWindow, int width, int height,
			String title, boolean resizable, boolean flag1 )
	{
		frameOffset = 28;
		frameWidth = width;
		frameHeight = height;
		aGameWindow = gameWindow;

		if( flag1 )
		{
			frameOffset = 48;
		}
		else
		{
			frameOffset = 28;
		}

		setTitle( title );
		setResizable( resizable );
		createMenuBar();
		setVisible( true );
		toFront();
		setNewSize( frameWidth, frameHeight );

		aGraphics49 = getGraphics();
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setMudClient( mudclient cl )
	{
		client = cl;
	}

	// -------------------------------------------------------------------------------------------------------------------
	private void createMenuBar()
	{
		mainMenu = new MenuBar();
		setMenuBar( mainMenu );

		// file menu
		fileMenu = new Menu( "File" );
		mainMenu.add( fileMenu );

		ExtendedMenuItem menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "Open") );
		menuItem.setEventID( MenuEvent.FILE_OPEN);
		menuItem.addActionListener( this );

		fileMenu.add( menuItem = new ExtendedMenuItem( "Save") );
		menuItem.setEventID( MenuEvent.FILE_SAVE );
		menuItem.addActionListener( this );

		fileMenu.addSeparator();

		fileMenu.add( menuItem = new ExtendedMenuItem( "Exit") );
		menuItem.setEventID( MenuEvent.FILE_EXIT );
		menuItem.addActionListener( this );


		fileMenu = new Menu( "Editor" );
		mainMenu.add( fileMenu );

		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "Reload Section") );
		menuItem.setEventID( MenuEvent.EDITOR_RELOAD );
		menuItem.addActionListener( this );
		
		fileMenu = new Menu( "Move" );
		mainMenu.add( fileMenu );

		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "Up") );
		menuItem.setEventID( MenuEvent.MOVE_UP );
		menuItem.addActionListener( this );
		
		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "Down") );
		menuItem.setEventID( MenuEvent.MOVE_DOWN );
		menuItem.addActionListener( this );
		
		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "North") );
		menuItem.setEventID( MenuEvent.MOVE_NORTH );
		menuItem.addActionListener( this );
		
		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "East") );
		menuItem.setEventID( MenuEvent.MOVE_EAST );
		menuItem.addActionListener( this );
		
		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "South") );
		menuItem.setEventID( MenuEvent.MOVE_SOUTH );
		menuItem.addActionListener( this );
		
		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "West") );
		menuItem.setEventID( MenuEvent.MOVE_WEST );
		menuItem.addActionListener( this );
		
		fileMenu = new Menu( "View" );
		mainMenu.add( fileMenu );

		menuItem = null;
		fileMenu.add( roofViews = new ExtendedMenuItem( "Show Roofs") );
		menuItem = (ExtendedMenuItem) roofViews;
		menuItem.setEventID( MenuEvent.VIEW_ROOFS );
		menuItem.addActionListener( this );


		fileMenu = new Menu( "Mode" );
		mainMenu.add( fileMenu );

		menuItem = null;
		fileMenu.add( editModes[0] = new ExtendedMenuItem( "√ View Only") );
		menuItem = (ExtendedMenuItem) editModes[0];
		menuItem.setEventID( MenuEvent.MODE_VIEW );
		menuItem.addActionListener( this );
		
		menuItem = null;
		fileMenu.add( editModes[1] = new ExtendedMenuItem( "Land") );
		menuItem = (ExtendedMenuItem) editModes[1];
		menuItem.setEventID( MenuEvent.MODE_LAND );
		menuItem.addActionListener( this );
		
		fileMenu = new Menu( "Help" );
		mainMenu.add( fileMenu );

		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "About") );
		menuItem.setEventID( MenuEvent.HELP_ABOUT );
		menuItem.addActionListener( this );

	}

	// handle menu action events
	// -------------------------------------------------------------------------------------------------------------------
	public void actionPerformed( ActionEvent evt )
	{
		ExtendedMenuItem menuItem = (ExtendedMenuItem) evt.getSource();
		if( menuItem == null )
			return;

		MenuEvent eventID = menuItem.getEventID();
		switch( eventID )
		{
			case FILE_EXIT:
				if( client.isModified )
				{
					Object[] options = { "Yes, please", "No, thanks", "Cancel" };
					int n = JOptionPane.showOptionDialog( this,
							"Would you like to save changes?", "Question",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[2] );

					switch( n )
					{
						case 0:
							client.saveSectors();
							break;

						case 2:
							return;
					}
				}
				System.exit( 0 );
				break;

			case FILE_OPEN:
				if( client == null )
					return;

				if( client.isModified )
				{
					Object[] options = { "Yes, please", "No, thanks", "Cancel" };
					int n = JOptionPane.showOptionDialog( this,
							"Would you like to save changes?", "Question",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[2] );

					switch( n )
					{
						case 0:
							client.saveSectors();
							break;

						case 2:
							return;
					}
				}

				// display sections dialog
				showSelectSectorsFrame();
				break;

			case FILE_SAVE:
				// save sectors
				System.out.println("Menu save triggered...");
				boolean success = client.saveSectors();
				System.out.println("Menu save " + (success ? "successful" : "failed"));
				break;
				
			case EDITOR_RELOAD:
				client.reloadSection( client.selectedSectionName );
				break;
				
			case MOVE_UP:
				client.sectionMove(4);
				break;
				
			case MOVE_DOWN:
				client.sectionMove(5);
				break;
				
			case MOVE_NORTH:
				client.sectionMove(0);
				break;
				
			case MOVE_EAST:
				client.sectionMove(1);
				break;
				
			case MOVE_SOUTH:
				client.sectionMove(2);
				break;
				
			case MOVE_WEST:
				client.sectionMove(3);
				break;
				
			case MODE_VIEW:
				resetEditModeNames();
				editModes[0].setLabel("√ View Only");
				client.editMode = 0;
				client.hideEditFrame();
				break;
				
			case MODE_LAND:
				resetEditModeNames();
				editModes[1].setLabel("√ Land");
				client.editMode = 1;
				client.showEditFrame();
				break;
				
			case VIEW_ROOFS:
				client.showRoof = !client.showRoof;
				if(client.showRoof)
					roofViews.setLabel("Hide Roofs");
				else
					roofViews.setLabel("Show Roofs");
				break;
				
			case HELP_ABOUT:
				JOptionPane.showMessageDialog(this, 
						"RSCD 3D Map Editor\n\n" +
						"Based on:\n" +
						"https://code.google.com/p/rscamap3deditor/\n",
						"About", JOptionPane.INFORMATION_MESSAGE);
				break;
				
			default:
				break;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public Graphics getGraphics()
	{
		Graphics g = super.getGraphics();
		if( graphicsTranslate == 0 )
		{
			g.translate( 0, 24 );
		}
		else
		{
			g.translate( -5, 0 );
		}

		return g;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setNewSize( int i, int j )
	{
		super.setSize( i, j + frameOffset );
	}

	// -------------------------------------------------------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	public boolean handleEvent( Event event )
	{
		if( event.id == 401 )
		{
			aGameWindow.keyDown( event, event.key );
		}
		else if( event.id == 402 )
		{
			aGameWindow.keyUp( event, event.key );
		}
		else if( event.id == 501 )
		{
			aGameWindow.mouseDown( event, event.x, event.y - 24 );
		}
		else if( event.id == 506 )
		{
			aGameWindow.mouseDrag( event, event.x, event.y - 24 );
		}
		else if( event.id == 502 )
		{
			aGameWindow.mouseUp( event, event.x, event.y - 24 );
		}
		else if( event.id == 503 )
		{
			aGameWindow.mouseMove( event, event.x, event.y - 24 );
		}
		else if( event.id == 201 )
		{
			aGameWindow.destroy();
		}
		else if( event.id == 1001 )
		{
			aGameWindow.action( event, event.target );
		}
		else if( event.id == 403 )
		{
			aGameWindow.keyDown( event, event.key );
		}
		else if( event.id == 404 )
		{
			aGameWindow.keyUp( event, event.key );
		}

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final void paint( Graphics g )
	{
		aGameWindow.paint( g );
	}
	
	private void resetEditModeNames()
	{
		editModes[0].setLabel("View Only");
		editModes[1].setLabel("Land");
	}

	// -------------------------------------------------------------------------------------------------------------------
	int frameWidth;
	int frameHeight;
	int graphicsTranslate;
	int frameOffset;
	GameWindow aGameWindow;
	Graphics aGraphics49;
}
