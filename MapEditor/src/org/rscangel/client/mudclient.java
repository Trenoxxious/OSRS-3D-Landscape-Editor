package org.rscangel.client;

import org.rscangel.client.entityhandling.EntityHandler;
import org.rscangel.client.model.Sector;
import org.rscangel.client.model.Sprite;
import org.rscangel.client.model.Tile;
import org.rscangel.client.util.Config;

import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.*;

public final class mudclient extends GameWindowMiddleMan
{
	private static final long serialVersionUID = 7526472295622776147L;

	// -------------------------------------------------------------------------------------------------------------------
	public static final int SPRITE_MEDIA_START = 2000;
	public static final int SPRITE_UTIL_START = 2100;
	public static final int SPRITE_ITEM_START = 2150;
	public static final int SPRITE_LOGO_START = 3150;
	public static final int SPRITE_PROJECTILE_START = 3160;
	public static final int SPRITE_TEXTURE_START = 3220;
	private final int MAX_CONFIG_SEGMENTS = 20;

	// -------------------------------------------------------------------------------------------------------------------
	private LinkedList<BufferedImage> frames = new LinkedList<BufferedImage>();
	private long lastFrame = 0;

	boolean realodCurrent = true;
	TileEditFrame tileEditFrame = null;

	// -------------------------------------------------------------------------------------------------------------------

	public String selectedSectionName = "";
	public boolean showRoof = false;
	public int editMode = 0;
	/* editmode, determines what happens on mouse click
	 * 0 = view only - nothing happens
	 * 1 = land editor
	 */

	// -------------------------------------------------------------------------------------------------------------------
	private int lastWalkTimeout;
	private int playerCount;
	private Mob npcArray[] = new Mob[500];
	private boolean prayerOn[] = new boolean[50];
	private Mob mobArray[] = new Mob[8000];
	private int wildX = 0;
	private int wildY = 0;
	private int lastWildYSubtract = 0;
	private boolean memoryError = false;
	private int magicLoc = 128;
	private int loggedIn;
	private int screenRotationX;
	private int loginTimer;
	private int areaX;
	private int areaY;
	private int wildYSubtract = -1;
	private boolean showCharacterLookScreen = false;
	private Model objectModelArray[] = new Model[1500];
	private int systemUpdate;
	private int cameraRotation = 128;
	private int logoutTimeout;
	int anInt826;
	private int actionPictureType;
	private int lastAutoCameraRotatePlayerX;
	private int lastAutoCameraRotatePlayerY;
	private int objectX[] = new int[1500];
	private int objectY[] = new int[1500];
	private int objectType[] = new int[1500];
	private int objectID[] = new int[1500];
	private Mob ourPlayer = new Mob();
	int sectionX = 0;
	int sectionY = 0;
	int sectionH = 0;
	int serverIndex = 0;
	private EngineHandle engineHandle;
	private Mob playerArray[] = new Mob[500];
	private int cameraHeight = 550;
	private int screenRotationY;
	private boolean cameraAutoAngleDebug = false;
	private Mob npcRecordArray[] = new Mob[8000];
	private GameImageMiddleMan gameGraphics;
	private boolean lastLoadedNull = false;
	private Camera gameCamera;
	int mouseClickXArray[] = new int[8192];
	int mouseClickYArray[] = new int[8192];
	private Graphics aGraphics936;
	private int modelUpdatingTimer;
	private int playerAliveTimeout;
	private int objectCount;
	private int cameraSizeInt = 9;
	private boolean freezScreen = false;
	int anInt981;
	long privateMessageTarget;

	private int sectionXPos = 0;
	private int sectionYPos = 0;
	private int worldXPos = 0;
	private int worldYPos = 0;
	private boolean resetMapEnterPos = true;
	private int dragStartPosX = 0;
	private int dragStartPosY = 0;
	private int dragEndPosX = 0;
	private int dragEndPosY = 0;

	private boolean sectionLoaded = false;
	public boolean isModified = false;

	private boolean mDragStart = false;
	private int mDragStartMousePosX = 0;
	private int mDragStartMousePosY = 0;
	private int mDragCurrentMousePosX = 0;
	private int mDragCurrentMousePosY = 0;
	
	private boolean middleMouseDrag = false;
	private int lastMiddleMouseX = 0;
	private int lastMiddleMouseY = 0;
	
	private boolean rightMouseDrag = false;
	private int lastRightMouseX = 0;
	private int rightMouseDownX = 0;
	private boolean actuallyDraggedRight = false;
	private boolean actuallyDraggedMiddle = false;
	
	// Tool panel variables
	private int activeTool = 0; // 0 = none, 1 = height tool, 2 = texture tool, 3 = overlay tool
	private int heightToolRadius = 3;
	private float heightToolSoftness = 0.5f;
	private int heightToolStrength = 5;
	private int savedEditMode = 0; // Store edit mode when dragging
	
	// Texture tool settings
	private int textureToolRadius = 2;
	private float textureToolSoftness = 0.5f;
	private int selectedTexture = 1; // Current texture ID to paint
	
	// Overlay tool settings - always single tile
	private int selectedOverlay = 0; // Current overlay type to paint
	
	// Current mouse position for tool previews
	private int currentMouseX = 0;
	private int currentMouseY = 0;
	
	// Rendering options
	private boolean shadowsEnabled = true;
	private boolean lockCameraHeight = false;
	private int fixedCameraHeight = -200; // Fixed camera height when locked
	
	// Undo system
	private java.util.List<UndoAction> undoHistory = new java.util.ArrayList<UndoAction>();
	private static final int MAX_UNDO_ACTIONS = 5;
	private long lastDragEndTime = 0; // Time when last drag ended

	private SectionsConfig config = new SectionsConfig( "recent" );
	
	// Inner class for undo actions
	private static class UndoAction
	{
		public int x, y;
		public byte oldGroundElevation, oldGroundTexture, oldGroundOverlay;
		public byte newGroundElevation, newGroundTexture, newGroundOverlay;
		public String actionType;
		
		public UndoAction(int x, int y, Tile oldTile, Tile newTile, String actionType)
		{
			this.x = x;
			this.y = y;
			this.oldGroundElevation = oldTile.groundElevation;
			this.oldGroundTexture = oldTile.groundTexture;
			this.oldGroundOverlay = oldTile.groundOverlay;
			this.newGroundElevation = newTile.groundElevation;
			this.newGroundTexture = newTile.groundTexture;
			this.newGroundOverlay = newTile.groundOverlay;
			this.actionType = actionType;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private mudclient()
	{
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void showEditFrame()
	{
		if( tileEditFrame == null )
		{
			tileEditFrame = new TileEditFrame();

			tileEditFrame.setVisible( true );
			tileEditFrame.setResizable( false );
			tileEditFrame.setAlwaysOnTop( true );
			tileEditFrame.setMudClient( this );
		}

		if( !tileEditFrame.isVisible() )
			tileEditFrame.setVisible( true );
	}
	
	public void hideEditFrame()
	{
		tileEditFrame.setVisible(false);
	}

	// -------------------------------------------------------------------------------------------------------------------
	private void setTileEditProperties( Tile tile, boolean clean )
	{
		if( tileEditFrame == null )
			return;

		if( clean )
			tileEditFrame.clean();

		if( tile == null )
			return;

		tileEditFrame.inspectTile( tile );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void updateRender( boolean modified )
	{
		isModified = modified;

		realodCurrent = false;
		freezScreen = true;
		loadSection( sectionX, sectionY );
		freezScreen = false;
	}

	// -------------------------------------------------------------------------------------------------------------------
	private Tile getTileAtPos( int x, int y )
	{
		byte byte0 = 0;

		if( x < 0 || x >= 96 || y < 0 || y >= 96 )
			return null;

		if( x >= 48 && y < 48 )
		{
			byte0 = 1;
			x -= 48;
		}
		else if( x < 48 && y >= 48 )
		{
			byte0 = 2;
			y -= 48;
		}
		else if( x >= 48 && y >= 48 )
		{
			byte0 = 3;
			x -= 48;
			y -= 48;
		}

		Sector sector = engineHandle.sectors[byte0];
		if( sector == null )
			return null;

		Tile ret = sector.getTile( x, y );
		if( ret != null )
		{
			ret.setName( "s" + byte0 + "x" + x + "y" + y );
		}
		return ret;
	}
	// -------------------------------------------------------------------------------------------------------------------
	private int _diagonalWallsEdit = 0;
	private byte _groundElevEdit = 0;
	private byte _groundOverEdit = 0;
	private byte _groundTexEdit = 0;
	private byte _horizontalWallsEdit = 0;
	private byte _roofEdit = 0;
	private byte _verticalWallsEdit = 0;

	public int diagonalWallsEdit = 1;
	public byte groundElevEdit = 0;
	public byte groundOverEdit = 0;
	public byte groundTexEdit = 0;
	public byte horizontalWallsEdit = 0;
	public byte roofEdit = 0;
	public byte verticalWallsEdit = 0;

	private void rememberTileValue(Tile tile)
		{
		_diagonalWallsEdit = tile.diagonalWalls;
		_groundElevEdit = tile.groundElevation;
		if( tile.mIsEmpty )
			{
			_groundOverEdit = tile.mDefaultGroundOverlay;
			}
		else
			{
			_groundOverEdit = tile.groundOverlay;
			}
		_groundTexEdit = tile.groundTexture;
		_horizontalWallsEdit = tile.horizontalWall;
		_roofEdit = tile.roofTexture;
		_verticalWallsEdit = tile.verticalWall;
		}
	public void updateTile(Tile tileElement)
		{
		if( tileElement == null )
			return;
		tileElement.diagonalWalls = diagonalWallsEdit;
		tileElement.groundElevation = groundElevEdit;
		tileElement.groundOverlay = groundOverEdit;
		tileElement.groundTexture = groundTexEdit;
		tileElement.horizontalWall = horizontalWallsEdit;
		tileElement.roofTexture = roofEdit;
		tileElement.verticalWall = verticalWallsEdit;
		tileElement.mIsEmpty = false;
		}

	// -------------------------------------------------------------------------------------------------------------------
	private void editElevation( int x, int y )
	{
		if( !sectionLoaded )
			return;

		//showEditFrame();

			Tile tile = getTileAtPos( x, y );
			byte byte0 = 0;

			if( x >= 48 && y < 48 )
			{
				byte0 = 1;
				x -= 48;
			}
			else if( x < 48 && y >= 48 )
			{
				byte0 = 2;
				y -= 48;
			}
			else if( x >= 48 && y >= 48 )
			{
				byte0 = 3;
				x -= 48;
				y -= 48;
			}

			Sector sector = engineHandle.sectors[byte0];
			if( tile == null )
				System.out.println( "Cannot obtain tile at coordinates: " + x
						+ ":" + y );
			rememberTileValue(tile);
			updateTile(tile);
			sector.setTile(x, y, tile);
			updateRender( true );
			//setTileEditProperties( tile, true );
	}
	public void reloadTile(Tile tile)
	{

	}
	
	private int getTileSectionNumber(Tile tile)
	{
		String name = tile.getTileName();
		
		int sIndex = name.lastIndexOf('s');
		int xIndex = name.lastIndexOf('x');
		
		return Integer.parseInt(name.substring(sIndex + 1, xIndex));
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void reloadSection( String sectionName ) // update edit siin pets
	{
		
		sectionLoaded = false;
		isModified = false;
		int x = 0;
		int y = 0;
		int h = 0;

		int hIndex = sectionName.lastIndexOf( 'h' );
		int xIndex = sectionName.lastIndexOf( 'x' );
		int yIndex = sectionName.lastIndexOf( 'y' );

		if( hIndex < 0 || xIndex < 0 || yIndex < 0 )
			return;

		try
		{
			h = wildYSubtract = Integer.parseInt( sectionName.substring(
					hIndex + 1, xIndex ) );
			x = Integer.parseInt( sectionName.substring( xIndex + 1, yIndex ) );
			y = Integer.parseInt( sectionName.substring( yIndex + 1,
					sectionName.length() ) );
		}
		catch( Exception e )
		{
			return;
		}
		
		if(x < 1)
			x = 1;
		if(y < 1)
			y = 1;
		
		sectionName = "h" + h + "x" + x + "y" + y;
		
		sectionX = x;
		sectionY = y;
		sectionH = h;

		wildX = ((x + 1) * 48) - 24;
		wildY = ((y + 1) * 48) - 24;
		
		realodCurrent = true;
			
		// clean engine
		for( int i = 0; i < engineHandle.sectors.length; i++ )
			engineHandle.sectors[i] = null;

		// load section
		sectionLoaded = loadSection( x, y );
		
		//int mapEnterX = sectionX * magicLoc + 64;
		//int mapEnterY = sectionY * magicLoc + 64;
		int mapEnterX = -1;
		int mapEnterY = -1;
		if(resetMapEnterPos)
		{
			mapEnterX = 6144; //128 tiles * 96 tiles in 2 sections * 128 units per tile / 2
			mapEnterY = 6144;
		}
		else
		{
			mapEnterX = ourPlayer.currentX;
			mapEnterY = ourPlayer.currentY;
		}
		resetMapEnterPos = false;
		
		if( sectionLoaded )
		{
			ourPlayer.waypointCurrent = 0;
			ourPlayer.waypointEndSprite = 0;
			ourPlayer.currentX = ourPlayer.waypointsX[0] = mapEnterX;
			ourPlayer.currentY = ourPlayer.waypointsY[0] = mapEnterY;
		}

		ourPlayer = makePlayer( serverIndex, mapEnterX, mapEnterY, 1 );
		// reset global variables for rendering
		resetVars();

		gameFrame.setTitle( DEFAULT_WINDOW_TITLE + " - "  + sectionName);
		selectedSectionName = sectionName;
	}

	// -------------------------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public String[] getSectionNames()
	{
		ZipFile tileArchive = engineHandle.getTileArchive();
		if( tileArchive == null )
			return new String[0];

		String[] sections = new String[tileArchive.size()];

		try
		{
			Enumeration entries = tileArchive.entries();
			ZipEntry entry = null;

			int i = 0;
			while( entries.hasMoreElements() )
			{
				entry = (ZipEntry) entries.nextElement();
				if( entry == null )
					continue;

				sections[i] = entry.getName();
				i++;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return sections;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public String[] getLastSectionNames()
	{
		String[] sections = new String[MAX_CONFIG_SEGMENTS];
		sections = config.getSetionsList();
		return sections;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setLastSectionName( String sectionName )
	{
		config.setLastSection( sectionName );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public SectionsConfig getConfig()
	{
		return config;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public static final void main( String[] args ) throws Exception
	{
		Config.initConfig( args.length > 0 ? args[0] : "settings.ini" );
		GameWindowMiddleMan.clientVersion = 1;

		mudclient mc = new mudclient();
		mc.setLogo( Toolkit.getDefaultToolkit().getImage( Config.CLIENT_DIR + File.separator + "Loading.rscd" ) );
		mc.createWindow( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, DEFAULT_WINDOW_TITLE, false );
	}

	// -------------------------------------------------------------------------------------------------------------------
	private BufferedImage getImage() throws IOException
	{
		BufferedImage bufferedImage = new BufferedImage( DEFAULT_WINDOW_WIDTH,
				DEFAULT_WINDOW_HEIGHT + 11, BufferedImage.TYPE_INT_RGB );

		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage( gameGraphics.image, 0, 0, this );
		g2d.dispose();

		return bufferedImage;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final Graphics getGraphics()
	{
		if( GameWindow.gameFrame != null )
		{
			return GameWindow.gameFrame.getGraphics();
		}

		return super.getGraphics();
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void loadConfigFilter()
	{
		drawLoadingBarText( 15, "Unpacking Configuration" );
		EntityHandler.load();
	}

	// calculate mouse position in the world
	// -------------------------------------------------------------------------------------------------------------------
	private void calculateMousePosition( int x, int y )
	{
		if( gameCamera == null )
			return;

		// gameCamera.updateMouseCoords( x, y );
		// gameCamera.finishCamera();

		int j = -1;
		int i1 = gameCamera.getCurrentVisibleModelCount();
		Model models[] = gameCamera.getVisibleModels();

		// System.out.println( "Search visible objects" );
		for( int j1 = 0; j1 < i1; j1++ )
		{
			Model model = models[j1];
			int ai[] = gameCamera.getVisibleModelIntArray();
			int k1 = ai[j1];

			if( k1 >= 0 )
				k1 = model.anIntArray258[k1] - 0x30d40;

			if( k1 >= 0 )
				j = k1;

			int l1 = j;
			sectionXPos = engineHandle.selectedX[l1];
			sectionYPos = engineHandle.selectedY[l1];
			worldXPos = ((sectionX - 49) * 48) + sectionXPos;
			
			//Heights are y-shifted on the server to determine a unique y coord for different heights
			int worldYPosBeforeHeightYShift = ((sectionY - 38) * 48) + sectionYPos;
			worldYPos = (sectionH * Config.HEIGHT_Y_SHIFT) + (worldYPosBeforeHeightYShift % Config.HEIGHT_Y_SHIFT);
			// System.out.println( "Visible object found" );
		}
	}

    private final void setPixelsAndAroundColour(int x, int y, int colour) {
        gameGraphics.setPixelColour(x, y, colour);
        gameGraphics.setPixelColour(x - 1, y, colour);
        gameGraphics.setPixelColour(x + 1, y, colour);
        gameGraphics.setPixelColour(x, y - 1, colour);
        gameGraphics.setPixelColour(x, y + 1, colour);
    }

	protected void handleMouseMove( int button, int x, int y)
		{
		calculateMousePosition( x, y );
		currentMouseX = x;
		currentMouseY = y;
		}

	// game window mouse down handler
	// -------------------------------------------------------------------------------------------------------------------
	protected void handleMouseUp( int button, int x, int y )
	{
		System.out.println("Mouse up: button=" + button + ", x=" + x + ", y=" + y + ", editMode=" + editMode + ", activeTool=" + activeTool + ", rightMouseDrag=" + rightMouseDrag + ", middleMouseDrag=" + middleMouseDrag + ", actuallyDraggedRight=" + actuallyDraggedRight + ", actuallyDraggedMiddle=" + actuallyDraggedMiddle);
		
		// Handle fake button 1 events that are actually middle mouse up (Java Event bug)
		if( button == 1 && middleMouseDrag )
		{
			middleMouseDrag = false;
			actuallyDraggedMiddle = false;
			System.out.println("Middle mouse up disguised as button 1 - ignoring");
			return;
		}
		if( button == 2 )
		{			
			rightMouseDrag = false;
			
			if( actuallyDraggedRight )
			{
				// We were dragging - don't do any height tool actions
				actuallyDraggedRight = false;
				System.out.println("Right drag ended - no tool action");
				return; // Exit early to prevent any tool actions after drag
			}
			else if (activeTool == 1)
			{
				// Height tool - right click lowers (only if we weren't dragging)
				System.out.println("Height tool: lowering at " + sectionXPos + ", " + sectionYPos);
				paintHeight(sectionXPos, sectionYPos, false);
			}
			else if (activeTool == 2)
			{
				// Texture tool - right click paints texture
				paintTexture(sectionXPos, sectionYPos);
			}
			else if (activeTool == 3)
			{
				// Overlay tool - right click paints overlay
				paintOverlay(sectionXPos, sectionYPos);
			}
			else if(editMode == 1)
			{
				// Right-click without drag - do tile editing
				Tile tile = getTileAtPos( sectionXPos, sectionYPos );
				if( tile == null )
					System.out.println( "Cannot obtain tile at coordinates: " + x + ":" + y );
				setTileEditProperties( tile, false );
			}
		}
		else if( button == 3 )
		{
			middleMouseDrag = false;
			
			if( actuallyDraggedMiddle )
			{
				// We were dragging - don't do any tool actions ever for middle mouse
				actuallyDraggedMiddle = false;
				System.out.println("Middle drag ended - no tool action (middle mouse never edits terrain)");
				return; // Exit early to prevent any tool actions
			}
			// Middle mouse should never edit terrain, even without dragging
			System.out.println("Middle mouse up - ignoring (middle mouse never edits terrain)");
			return;
		}
		
		// Handle legitimate left clicks (button 1) - tool panel and terrain editing
		if (button == 1)
		{
			// Tool panel clicks
			if (x >= 10 && x <= 210 && y >= 100 && y <= 300)
			{
				if (y >= 130 && y <= 150)
				{
					activeTool = (activeTool == 1) ? 0 : 1;
					editMode = activeTool;
					System.out.println("Height tool toggled: " + (activeTool == 1 ? "ON" : "OFF"));
				}
			}
			// Height tool terrain editing
			else if (activeTool == 1)
			{
				System.out.println("Height tool: raising at " + sectionXPos + ", " + sectionYPos);
				paintHeight(sectionXPos, sectionYPos, true);
			}
			// Texture tool - left click paints texture
			else if (activeTool == 2)
			{
				paintTexture(sectionXPos, sectionYPos);
			}
			// Overlay tool - left click paints overlay  
			else if (activeTool == 3)
			{
				paintOverlay(sectionXPos, sectionYPos);
			}
			// Legacy editing
			else if(editMode == 1)
			{
				editElevation( sectionXPos, sectionYPos );
			}
		}
		else if(editMode == 1)
		{
			// Legacy editing when no tool is active
			if (button == 1)
			{
				editElevation( sectionXPos, sectionYPos );
			}
		}
	}

	// game window mouse down handler
	// -------------------------------------------------------------------------------------------------------------------
	protected final void handleMouseDown( int button, int x, int y )
	{
		System.out.println("Mouse down: button=" + button + ", x=" + x + ", y=" + y);
		if( button == 2 )
		{
			rightMouseDrag = true;
			lastRightMouseX = x;
			rightMouseDownX = x; // Remember where we started
			actuallyDraggedRight = false; // Reset - will be set to true in handleMouseDrag
		}
		else if( button == 3 )
		{
			middleMouseDrag = true;
			lastMiddleMouseX = x;
			lastMiddleMouseY = y;
			actuallyDraggedMiddle = false; // Reset - will be set to true in handleMouseDrag
		}

			/*calculateMousePosition( x, y );
			editElevation( worldXPos, worldYPos );
		if( button == 1 && !mDragStart )
		{
			mDragStartMousePosX = x;
			mDragStartMousePosY = y;

			mDragCurrentMousePosX = x;
			mDragCurrentMousePosY = y;

			// int oldPosX = ourPlayer.currentX;
			// int oldPosY = ourPlayer.currentY;

			// ourPlayer.currentX = ourPlayer.waypointsX[0] = worldXPos;
			// ourPlayer.currentY = ourPlayer.waypointsY[0] = worldYPos;

			calculateMousePosition( x, y );

			// ourPlayer.currentX = ourPlayer.waypointsX[0] = oldPosX;
			// ourPlayer.currentY = ourPlayer.waypointsY[0] = oldPosY;

			dragStartPosX = worldXPos;
			dragStartPosY = worldYPos;

			mDragStart = true;
		}
		*/
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void handleMouseDrag( int button, int x, int y )
	{
		if( button == 2 && rightMouseDrag )
		{
			int deltaX = x - lastRightMouseX;
			
			// Rotate camera based on horizontal mouse movement
			cameraRotation = (cameraRotation - deltaX) & 0xff;
			
			lastRightMouseX = x;
			actuallyDraggedRight = true; // Mark that we actually dragged
		}
		else if( button == 3 && middleMouseDrag )
		{
			int deltaX = x - lastMiddleMouseX;
			int deltaY = y - lastMiddleMouseY;
			
			// Apply direction-specific transformations to maintain natural grab-and-drag feel
			int finalDeltaX = deltaX;
			int finalDeltaY = deltaY;
			
			// Adjust based on camera direction to maintain consistent panning feel
			if (cameraRotation >= 240 || cameraRotation < 16) { // South
				finalDeltaX = -deltaX;
				finalDeltaY = -deltaY;
			}
			else if (cameraRotation >= 176 && cameraRotation < 208) { // East
				finalDeltaX = -deltaY;
				finalDeltaY = deltaX;
			}
			else if (cameraRotation >= 48 && cameraRotation < 80) { // West
				finalDeltaX = deltaY;
				finalDeltaY = -deltaX;
			}
			
			ourPlayer.currentX += finalDeltaX * 2;
			ourPlayer.currentY -= finalDeltaY * 2;
			
			lastMiddleMouseX = x;
			lastMiddleMouseY = y;
			actuallyDraggedMiddle = true; // Mark that we actually dragged
		}
		else if( button == 1 && mDragStart )
		{
			mDragCurrentMousePosX = x;
			mDragCurrentMousePosY = y;

			// drawGame();
		}
	}

	// needs to start the game after login
	// -------------------------------------------------------------------------------------------------------------------
	protected final void method4()
	{
		try
		{
			if( loggedIn == 1 )
			{
				drawGame();
			}
			else
			{
				gameFrame.setMudClient( this );
				// gameFrame.openSectionWithDialog();
				gameFrame.showSelectSectorsFrame();
				resetVars();
			}
		}
		catch( OutOfMemoryError e )
		{
			garbageCollect();
			memoryError = true;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final Mob makePlayer( int mobArrayIndex, int x, int y, int sprite )
	{
		if( mobArray[mobArrayIndex] == null )
		{
			mobArray[mobArrayIndex] = new Mob();
			mobArray[mobArrayIndex].serverIndex = mobArrayIndex;
			mobArray[mobArrayIndex].mobIntUnknown = 0;
		}

		Mob mob = mobArray[mobArrayIndex];
		mob.serverIndex = mobArrayIndex;
		mob.waypointEndSprite = 0;
		mob.waypointCurrent = 0;
		mob.waypointsX[0] = mob.currentX = x;
		mob.waypointsY[0] = mob.currentY = y;
		mob.nextSprite = mob.currentSprite = sprite;
		mob.stepCount = 0;

		playerArray[playerCount++] = mob;
		return mob;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void method2()
	{
		if( memoryError )
			return;

		if( lastLoadedNull )
			return;

		try
		{
			loginTimer++;
			if( loggedIn == 0 )
			{
				super.lastActionTimeout = 0;
			}
			if( loggedIn == 1 )
			{
				super.lastActionTimeout++;
				processGame();
			}
		}
		catch( OutOfMemoryError _ex )
		{
			garbageCollect();
			memoryError = true;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void resetLoginVars()
	{
		loggedIn = 0;
		playerCount = 0;
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void drawGame()
	{
		if( freezScreen )
		{
			return;
		}

		long now = System.currentTimeMillis();

		/*
		if( now - lastFrame > (1000 / Config.MOVIE_FPS) && recording)
		{
			try
			{
				lastFrame = now;
				frames.add( getImage() );
			}
			catch( Exception e )
			{
			}
		}
		*/

		/*
		 * Roof remover code
		 * */
		if(sectionLoaded)
		{
			for(int i = 0; i< 64; i++)
			{
				gameCamera.removeModel(engineHandle.aModelArrayArray598[lastWildYSubtract][i]);
				if(showRoof)
					gameCamera.addModel(engineHandle.aModelArrayArray598[lastWildYSubtract][i]);
			}
		}
		/*
		for (int i = 0; i < 64; i++) {
			gameCamera.removeModel(engineHandle.aModelArrayArray598[lastWildYSubtract][i]);
            if (lastWildYSubtract == 0) {
                //gameCamera.removeModel(engineHandle.aModelArrayArray580[1][i]);
                //gameCamera.removeModel(engineHandle.aModelArrayArray598[1][i]);
                //gameCamera.removeModel(engineHandle.aModelArrayArray580[2][i]);
                //gameCamera.removeModel(engineHandle.aModelArrayArray598[2][i]);
            }
            if (lastWildYSubtract == 0 && (engineHandle.walkableValue[ourPlayer.currentX / 128][ourPlayer.currentY / 128] & 0x80) == 0) {
                if (showRoof) {
                    gameCamera.addModel(engineHandle.aModelArrayArray598[lastWildYSubtract][i]);
                    if (lastWildYSubtract == 0) {
                        //gameCamera.addModel(engineHandle.aModelArrayArray580[1][i]);
                        //gameCamera.addModel(engineHandle.aModelArrayArray598[1][i]);
                        //gameCamera.addModel(engineHandle.aModelArrayArray580[2][i]);
                        //gameCamera.addModel(engineHandle.aModelArrayArray598[2][i]);
                    }
                }
            }
        }
        */
        /*
		 * End Roof remover code
		 */
		
		if( showCharacterLookScreen )
		{
			return;
		}

		if( !engineHandle.playerIsAlive )
		{
			return;
		}

		gameGraphics.f1Toggle = false;
		gameGraphics.method211();
		gameGraphics.f1Toggle = super.keyF1Toggle;

		int l5 = lastAutoCameraRotatePlayerX + screenRotationX;
		int i8 = lastAutoCameraRotatePlayerY + screenRotationY;
		
		// Use fixed camera height when height tool is active to prevent bobbing
		int cameraY = (activeTool == 1) ? fixedCameraHeight : -engineHandle.getAveragedElevation( l5, i8 );
		
		gameCamera.setCamera( l5, cameraY, i8, 912, cameraRotation * 4, 0, cameraHeight * 2 );

		gameCamera.finishCamera();
		
		// Draw tool preview when active
		if (activeTool > 0 && !middleMouseDrag && !rightMouseDrag)
		{
			drawToolPreview();
		}
		
		if( mDragStart )
		{
			int minX = Math.min( mDragStartMousePosX, mDragCurrentMousePosX );
			int maxX = Math.max( mDragStartMousePosX, mDragCurrentMousePosX );

			int minY = Math.min( mDragStartMousePosY, mDragCurrentMousePosY );
			int maxY = Math.max( mDragStartMousePosY, mDragCurrentMousePosY );

			minX = Math.max( gameGraphics.getImageX(), minX );
			minX = Math.min( gameGraphics.getImageX()
					+ gameGraphics.getImageWidth(), minX );

			maxX = Math.max( gameGraphics.getImageX(), maxX );
			maxX = Math.min( gameGraphics.getImageX()
					+ gameGraphics.getImageWidth(), maxX );

			minY = Math.max( gameGraphics.getImageY(), minY );
			minY = Math.min( gameGraphics.getImageY()
					+ gameGraphics.getImageHeight(), minY );

			maxY = Math.max( gameGraphics.getImageY(), maxY );
			maxY = Math.min( gameGraphics.getImageY()
					+ gameGraphics.getImageHeight(), maxY );

			if( maxX > minX && maxY > minY )
			{
				gameGraphics.drawBoxEdge( minX, minY, Math.min( maxX - minX,
						gameGraphics.getImageWidth() - minX ), Math.min( maxY
						- minY, gameGraphics.getImageHeight() - minY ),
						0xffffff );
			}

		}
		gameGraphics.drawString("@whi@Mouse:" + sectionXPos + ", " + sectionYPos, 10, 40, 0, 0);
		gameGraphics.drawString("@whi@World:" + worldXPos + ", " + worldYPos, 10, 50, 0, 0);
		String facing = "";
		if(cameraRotation >= 240 || cameraRotation < 16)
			facing = "South";
		else if(cameraRotation >= 16 && cameraRotation < 48)
			facing = "South-West";
		else if(cameraRotation >= 48 && cameraRotation < 80)
			facing = "West";
		else if(cameraRotation >= 80 && cameraRotation < 112)
			facing = "North-West";
		else if(cameraRotation >= 112 && cameraRotation < 144)
			facing = "North";
		else if(cameraRotation >= 144 && cameraRotation < 176)
			facing = "North-East";
		else if(cameraRotation >= 176 && cameraRotation < 208)
			facing = "East";
		else if(cameraRotation >= 208 && cameraRotation < 240)
			facing = "South-East";
		gameGraphics.drawString("@whi@Facing:" + facing, 10, 60, 0, 0);
		
		// Tool panel on left side
		gameGraphics.drawBoxEdge(10, 100, 200, 200, 0xffffff);
		gameGraphics.drawString("@whi@Tools: " + (editMode == 1 ? "EDIT" : "VIEW"), 20, 120, 0, 0);
		
		// Tool buttons
		int heightToolColor = (activeTool == 1) ? 0x00ff00 : 0xffffff;
		gameGraphics.drawBoxEdge(20, 130, 170, 20, heightToolColor);
		gameGraphics.drawString((activeTool == 1 ? "@gre@" : "@whi@") + "Height Tool (H)", 25, 145, 0, 0);
		
		int textureToolColor = (activeTool == 2) ? 0x00ff00 : 0xffffff;
		gameGraphics.drawBoxEdge(20, 155, 170, 20, textureToolColor);
		gameGraphics.drawString((activeTool == 2 ? "@gre@" : "@whi@") + "Texture Tool (J)", 25, 170, 0, 0);
		
		int overlayToolColor = (activeTool == 3) ? 0x00ff00 : 0xffffff;
		gameGraphics.drawBoxEdge(20, 180, 170, 20, overlayToolColor);
		gameGraphics.drawString((activeTool == 3 ? "@gre@" : "@whi@") + "Overlay Tool (K)", 25, 195, 0, 0);
		
		// Tool-specific settings
		if (activeTool == 1)
		{
			gameGraphics.drawString("@whi@Radius: " + heightToolRadius + " ([/])", 25, 215, 0, 0);
			gameGraphics.drawString("@whi@Strength: " + heightToolStrength + " (T/G)", 25, 225, 0, 0);
			gameGraphics.drawString("@whi@Softness: " + String.format("%.1f", heightToolSoftness) + " (Y/U)", 25, 235, 0, 0);
			gameGraphics.drawString("@whi@L-Click: Raise, R-Click: Lower", 25, 255, 0, 0);
		}
		else if (activeTool == 2)
		{
			gameGraphics.drawString("@whi@Radius: " + textureToolRadius + " ([/])", 25, 215, 0, 0);
			gameGraphics.drawString("@whi@Softness: " + String.format("%.1f", textureToolSoftness) + " (Y/U)", 25, 225, 0, 0);
			gameGraphics.drawString("@whi@Texture: " + selectedTexture + " (Q/E)", 25, 235, 0, 0);
			gameGraphics.drawString("@whi@Click: Paint Texture", 25, 255, 0, 0);
		}
		else if (activeTool == 3)
		{
			gameGraphics.drawString("@whi@Single Tile Only", 25, 215, 0, 0);
			gameGraphics.drawString("@whi@Overlay: " + selectedOverlay + " (Q/E)", 25, 225, 0, 0);
			gameGraphics.drawString("@whi@0=Ground 8=Water 11=Lava", 25, 235, 0, 0);
			gameGraphics.drawString("@whi@Click: Paint Overlay", 25, 255, 0, 0);
		}
		
		// Helper text in top-right corner
		int rightAlign = gameGraphics.getImageWidth() - 200;
		gameGraphics.drawString("@whi@Controls:", rightAlign, 40, 0, 0);
		gameGraphics.drawString("@whi@WASD - Move", rightAlign, 50, 0, 0);
		gameGraphics.drawString("@whi@Middle+Drag - Pan", rightAlign, 60, 0, 0);
		gameGraphics.drawString("@whi@Right+Drag - Rotate", rightAlign, 70, 0, 0);
		gameGraphics.drawString("@whi@Arrow Keys - Zoom/Tilt", rightAlign, 80, 0, 0);
		gameGraphics.drawString("@whi@S - Toggle Shadows " + (shadowsEnabled ? "ON" : "OFF"), rightAlign, 90, 0, 0);
		gameGraphics.drawImage( aGraphics936, 0, 0 );
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void resetIntVars()
	{
		systemUpdate = 0;
		loggedIn = 0;
		logoutTimeout = 0;
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void garbageCollect()
	{
		try
		{
			if( gameGraphics != null )
			{
				gameGraphics.cleanupSprites();
				gameGraphics.imagePixelArray = null;
				gameGraphics = null;
			}

			if( gameCamera != null )
			{
				gameCamera.cleanupModels();
				gameCamera = null;
			}

			objectModelArray = null;
			mobArray = null;
			playerArray = null;
			npcRecordArray = null;
			npcArray = null;
			ourPlayer = null;

			if( engineHandle != null )
			{
				engineHandle.aModelArray596 = null;
				engineHandle.aModelArrayArray580 = null;
				engineHandle.aModelArrayArray598 = null;
				engineHandle.aModel = null;
				engineHandle = null;
			}

			System.gc();
			return;
		}
		catch( Exception _ex )
		{
			return;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void startGame()
	{
		super.yOffset = 0;
		loadConfigFilter(); // 15%

		if( lastLoadedNull )
			return;

		aGraphics936 = getGraphics();
		changeThreadSleepModifier( 50 );
		gameGraphics = new GameImageMiddleMan( DEFAULT_WINDOW_WIDTH,
				DEFAULT_WINDOW_HEIGHT + 12 + 99, 4000, this );
		gameGraphics._mudclient = this;
		gameGraphics.setDimensions( 0, 0, DEFAULT_WINDOW_WIDTH,
				DEFAULT_WINDOW_HEIGHT + 12 );
		Menu.aBoolean220 = false;


		if( lastLoadedNull )
			return;

		gameCamera = new Camera( gameGraphics, 15000, 15000, 1000 );
		gameCamera.setCameraSize( DEFAULT_WINDOW_WIDTH / 2,
				DEFAULT_WINDOW_HEIGHT / 2, DEFAULT_WINDOW_WIDTH / 2,
				DEFAULT_WINDOW_HEIGHT / 2, DEFAULT_WINDOW_WIDTH, cameraSizeInt );
		gameCamera.zoom1 = 23000;
		gameCamera.zoom2 = 23000;
		gameCamera.zoom3 = 10;
		gameCamera.zoom4 = 21000;

		engineHandle = new EngineHandle( gameCamera, gameGraphics );
		loadTextures(); // 60%

		if( lastLoadedNull )
			return;

		drawLoadingBarText( 100, "Starting game..." );
		resetLoginVars();
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void loadSprite( int id, String packageName, int amount )
	{
		for( int i = id; i < id + amount; i++ )
		{
			if( !gameGraphics.loadSprite( i, packageName ) )
			{
				lastLoadedNull = true;
				return;
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void loadMedia()
	{
		drawLoadingBarText( 30, "Unpacking media" );

		int i = EntityHandler.invPictureCount();

		for( int j = 1; i > 0; j++ )
		{
			int k = i;
			i -= 30;

			if( k > 30 )
			{
				k = 30;
			}

			loadSprite( SPRITE_ITEM_START + (j - 1) * 30, "media.object", k );
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void loadTextures()
	{
		drawLoadingBarText( 60, "Unpacking textures" );
		gameCamera.method297( EntityHandler.textureCount(), 7, 11 );

		for( int i = 0; i < EntityHandler.textureCount(); i++ )
		{
			loadSprite( SPRITE_TEXTURE_START + i, "texture", 1 );
			Sprite sprite = ((GameImage) (gameGraphics)).sprites[SPRITE_TEXTURE_START
					+ i];

			int length = sprite.getWidth() * sprite.getHeight();
			int[] pixels = sprite.getPixels();
			int ai1[] = new int[32768];

			for( int k = 0; k < length; k++ )
			{
				ai1[((pixels[k] & 0xf80000) >> 9) + ((pixels[k] & 0xf800) >> 6)
						+ ((pixels[k] & 0xf8) >> 3)]++;
			}

			int[] dictionary = new int[256];
			dictionary[0] = 0xff00ff;
			int[] temp = new int[256];

			for( int i1 = 0; i1 < ai1.length; i1++ )
			{
				int j1 = ai1[i1];

				if( j1 > temp[255] )
				{
					for( int k1 = 1; k1 < 256; k1++ )
					{
						if( j1 <= temp[k1] )
							continue;

						for( int i2 = 255; i2 > k1; i2-- )
						{
							dictionary[i2] = dictionary[i2 - 1];
							temp[i2] = temp[i2 - 1];
						}

						dictionary[k1] = ((i1 & 0x7c00) << 9)
								+ ((i1 & 0x3e0) << 6) + ((i1 & 0x1f) << 3)
								+ 0x40404;
						temp[k1] = j1;
						break;
					}
				}

				ai1[i1] = -1;
			}

			byte[] indices = new byte[length];

			for( int l1 = 0; l1 < length; l1++ )
			{
				int j2 = pixels[l1];
				int k2 = ((j2 & 0xf80000) >> 9) + ((j2 & 0xf800) >> 6)
						+ ((j2 & 0xf8) >> 3);
				int l2 = ai1[k2];

				if( l2 == -1 )
				{
					int i3 = 0x3b9ac9ff;
					int j3 = j2 >> 16 & 0xff;
					int k3 = j2 >> 8 & 0xff;
					int l3 = j2 & 0xff;

					for( int i4 = 0; i4 < 256; i4++ )
					{
						int j4 = dictionary[i4];
						int k4 = j4 >> 16 & 0xff;
						int l4 = j4 >> 8 & 0xff;
						int i5 = j4 & 0xff;
						int j5 = (j3 - k4) * (j3 - k4) + (k3 - l4) * (k3 - l4)
								+ (l3 - i5) * (l3 - i5);
						if( j5 < i3 )
						{
							i3 = j5;
							l2 = i4;
						}
					}

					ai1[k2] = l2;
				}

				indices[l1] = (byte) l2;
			}

			gameCamera.method298( i, indices, dictionary, sprite
					.getSomething1() / 64 - 1 );
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final boolean loadSection( int x, int y )
	{
		engineHandle.playerIsAlive = false;

		if(x < 1)
			x = 1;
		if(y < 1)
			y = 1;
		//x += wildX;
		//y += wildY;
		//int i1 = (x + 24) / 48;
		//int j1 = (y + 24) / 48;
		lastWildYSubtract = wildYSubtract;
		//areaX = i1 * 48 - 48;
		//areaY = j1 * 48 - 48;

		engineHandle.method401( x, y, lastWildYSubtract, realodCurrent );

		areaX -= wildX;
		areaY -= wildY;
		engineHandle.playerIsAlive = true;

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final byte[] load( String filename )
	{
		return super.load( Config.CLIENT_DIR + File.separator + "data"
				+ File.separator + filename );
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void processGame()
	{
		if( systemUpdate > 1 )
			systemUpdate--;

		if( logoutTimeout > 0 )
			logoutTimeout--;

		if( ourPlayer.currentSprite == 8 || ourPlayer.currentSprite == 9 )
			lastWalkTimeout = 500;

		if( lastWalkTimeout > 0 )
			lastWalkTimeout--;

		if( showCharacterLookScreen )
			return;

		if( cameraAutoAngleDebug )
		{
			if( lastAutoCameraRotatePlayerX - ourPlayer.currentX < -500
					|| lastAutoCameraRotatePlayerX - ourPlayer.currentX > 500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY < -500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY > 500 )
			{
				lastAutoCameraRotatePlayerX = ourPlayer.currentX;
				lastAutoCameraRotatePlayerY = ourPlayer.currentY;
			}
		}
		else
		{
			lastAutoCameraRotatePlayerX = ourPlayer.currentX;
			lastAutoCameraRotatePlayerY = ourPlayer.currentY;
			/*if( lastAutoCameraRotatePlayerX - ourPlayer.currentX < -500
					|| lastAutoCameraRotatePlayerX - ourPlayer.currentX > 500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY < -500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY > 500 )
			{
				lastAutoCameraRotatePlayerX = ourPlayer.currentX;
				lastAutoCameraRotatePlayerY = ourPlayer.currentY;
			}

			if( lastAutoCameraRotatePlayerX != ourPlayer.currentX )
			{
				int scaleMovementDivisor = (16 + (cameraHeight - 500) / 15);
				if(scaleMovementDivisor < 1) //cannot divide by zero, negative numbers cause camera to spaz
					scaleMovementDivisor = 1;
				lastAutoCameraRotatePlayerX += (ourPlayer.currentX - lastAutoCameraRotatePlayerX) / scaleMovementDivisor;
			}
			if( lastAutoCameraRotatePlayerY != ourPlayer.currentY )
			{
				int scaleMovementDivisor = (16 + (cameraHeight - 500) / 15);
				if(scaleMovementDivisor < 1) //cannot divide by zero, negative numbers cause camera to spaz
					scaleMovementDivisor = 1;
				lastAutoCameraRotatePlayerY += (ourPlayer.currentY - lastAutoCameraRotatePlayerY) / scaleMovementDivisor;
			}*/
		}
		calculateMousePosition(mouseX, mouseY);

		if( playerAliveTimeout != 0 )
			super.lastMouseDownButton = 0;

		gameCamera.updateMouseCoords( super.mouseX, super.mouseY );
		super.lastMouseDownButton = 0;

		//Camera uses byte angles, 0 degrees is facing South, 128 is North
		if( super.keyLeftDown )
		{
			cameraRotation = cameraRotation + 2 & 0xff;
		}
		else if( super.keyRightDown )
		{
			cameraRotation = cameraRotation - 2 & 0xff;
		}
		else if( super.keyUpDown )
		{
			cameraHeight -= 10;
			if(cameraHeight < 100)
				cameraHeight = 100;
		}
		else if( super.keyDownDown )
		{
			cameraHeight += 10;
			if(cameraHeight > 10000)
				cameraHeight = 10000;
		}
		else if( super.keyWDown )
		{
			calculateCameraMove(cameraRotation);
			//ourPlayer.currentX += 10; //move west
		}
		else if( super.keySDown )
		{
			calculateCameraMove(cameraRotation + 128);
			//ourPlayer.currentX -= 10; //move east
		}

		if( super.keyADown )
		{
			calculateCameraMove(cameraRotation - 64);
			//ourPlayer.currentY += 10; //move south
		}
		if( super.keyDDown )
		{
			calculateCameraMove(cameraRotation + 64);
			//ourPlayer.currentY -= 10; //move north
			// for testing
		}
		
		// Handle tool shortcuts
		handleToolShortcuts();

		if( actionPictureType > 0 )
		{
			actionPictureType--;
		}
		else if( actionPictureType < 0 )
		{
			actionPictureType++;
		}

		modelUpdatingTimer++;
		if( modelUpdatingTimer > 5 )
			modelUpdatingTimer = 0;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void resetVars()
	{
		systemUpdate = 0;
		logoutTimeout = 0;
		loggedIn = 1;
		gameGraphics.method211();
		if( freezScreen == false )
		{
			gameGraphics.drawImage( aGraphics936, 0, 0 );
		}

		for( int i = 0; i < objectCount; i++ )
		{
			gameCamera.removeModel( objectModelArray[i] );
			engineHandle.updateObject( objectX[i], objectY[i], objectType[i],
					objectID[i] );
		}

		objectCount = 0;
		playerCount = 0;

		for( int k = 0; k < mobArray.length; k++ )
			mobArray[k] = null;

		for( int l = 0; l < playerArray.length; l++ )
			playerArray[l] = null;

		for( int i1 = 0; i1 < npcRecordArray.length; i1++ )
			npcRecordArray[i1] = null;

		for( int j1 = 0; j1 < npcArray.length; j1++ )
			npcArray[j1] = null;

		for( int k1 = 0; k1 < prayerOn.length; k1++ )
			prayerOn[k1] = false;

		super.lastMouseDownButton = 0;
		super.mouseDownButton = 0;
		super.friendsCount = 0;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final Image createImage( int i, int j )
	{
		if( GameWindow.gameFrame != null )
			return GameWindow.gameFrame.createImage( i, j );

		return super.createImage( i, j );
	}
	
	// -------------------------------------------------------------------------------------------------------------------
	public boolean saveSectors()
	{
		SectorSaver saver = new SectorSaver( engineHandle );
		isModified = false;

		return saver.save();
	}
	
	/**
	 * Calculates the movement of the camera based on the byte angle of the vector of movement.
	 * @param byteAngle - The angle of the vector you are moving on.
	 */
	private void calculateCameraMove(int byteAngle)
	{
		int x = (int) (10 * Math.sin(Math.toRadians(byteAngleToDegrees(byteAngle))));
		int y = (int) (10 * Math.cos(Math.toRadians(byteAngleToDegrees(byteAngle))));
		ourPlayer.currentX += x;
		ourPlayer.currentY += y;
	}
	
	/**
	 * Converts a byte angle to degrees. Byte angles larger than 255 will be converted into lowest represntation of degrees
	 * @param byteAngle - The byte angle to convert into degrees
	 * @return A floating point representation of the byte angle.
	 */
	private float byteAngleToDegrees(int byteAngle)
	{
		int ba = byteAngle % 255;
		float angle = ((ba / 256f) * 360);
		return angle;
	}
	
	// Undo system methods
	private void addUndoAction(UndoAction action)
	{
		undoHistory.add(0, action); // Add to front of list
		
		// Keep only the last MAX_UNDO_ACTIONS
		while (undoHistory.size() > MAX_UNDO_ACTIONS)
		{
			undoHistory.remove(undoHistory.size() - 1);
		}
		
		System.out.println("Added undo action: " + action.actionType + " at " + action.x + "," + action.y);
	}
	
	private void performUndo()
	{
		if (undoHistory.isEmpty())
		{
			System.out.println("No actions to undo");
			return;
		}
		
		UndoAction action = undoHistory.remove(0);
		System.out.println("Undoing " + action.actionType + " action at " + action.x + "," + action.y);
		
		Tile tile = getTileAtPos(action.x, action.y);
		if (tile != null)
		{
			// Restore original values
			tile.groundElevation = action.oldGroundElevation;
			tile.groundTexture = action.oldGroundTexture;
			tile.groundOverlay = action.oldGroundOverlay;
			
			// Update the sector
			byte sectorIndex = 0;
			int sectorX = action.x, sectorY = action.y;
			
			if (action.x >= 48 && action.y < 48)
			{
				sectorIndex = 1;
				sectorX -= 48;
			}
			else if (action.x < 48 && action.y >= 48)
			{
				sectorIndex = 2;
				sectorY -= 48;
			}
			else if (action.x >= 48 && action.y >= 48)
			{
				sectorIndex = 3;
				sectorX -= 48;
				sectorY -= 48;
			}
			
			engineHandle.sectors[sectorIndex].setTile(sectorX, sectorY, tile);
			updateRender(true);
			isModified = true;
		}
	}
	
	// Handle tool shortcuts based on key state
	private void handleToolShortcuts()
	{
		if (super.keyDown == 'h' || super.keyDown == 'H')
		{
			activeTool = (activeTool == 1) ? 0 : 1;
			editMode = (activeTool > 0) ? 1 : 0; // Auto-manage edit mode
		}
		else if (super.keyDown == 'j' || super.keyDown == 'J')
		{
			activeTool = (activeTool == 2) ? 0 : 2; // Texture tool
			editMode = (activeTool > 0) ? 1 : 0;
		}
		else if (super.keyDown == 'k' || super.keyDown == 'K')
		{
			activeTool = (activeTool == 3) ? 0 : 3; // Overlay tool
			editMode = (activeTool > 0) ? 1 : 0;
			super.keyDown = 0; // Clear key to prevent repeat
		}
		
		// Handle cursor and camera for all tools after activation
		if (activeTool == 1)
		{
			// Store current terrain-relative height as fixed height for height tool
			int l5 = lastAutoCameraRotatePlayerX + screenRotationX;
			int i8 = lastAutoCameraRotatePlayerY + screenRotationY;
			fixedCameraHeight = -engineHandle.getAveragedElevation( l5, i8 );
		}
		
		if (activeTool > 0)
		{
			// Create invisible cursor for all tools
			int[] pixels = new int[16 * 16];
			Image image = createImage(new java.awt.image.MemoryImageSource(16, 16, pixels, 0, 16));
			Cursor invisibleCursor = getToolkit().createCustomCursor(image, new Point(0, 0), "invisible");
			setCursor(invisibleCursor);
		}
		else
		{
			setCursor(Cursor.getDefaultCursor());
		}
		
		if (activeTool == 1) // Height tool controls
		{
			if (super.keyDown == ']')
			{
				heightToolRadius = Math.min(10, heightToolRadius + 1);
				super.keyDown = 0;
			}
			else if (super.keyDown == '[')
			{
				heightToolRadius = Math.max(1, heightToolRadius - 1);
				super.keyDown = 0;
			}
			else if (super.keyDown == 't' || super.keyDown == 'T')
			{
				heightToolStrength = Math.min(20, heightToolStrength + 1);
				super.keyDown = 0;
			}
			else if (super.keyDown == 'g' || super.keyDown == 'G')
			{
				heightToolStrength = Math.max(1, heightToolStrength - 1);
				super.keyDown = 0;
			}
			else if (super.keyDown == 'y' || super.keyDown == 'Y')
			{
				heightToolSoftness = Math.min(1.0f, heightToolSoftness + 0.1f);
				super.keyDown = 0;
			}
			else if (super.keyDown == 'u' || super.keyDown == 'U')
			{
				heightToolSoftness = Math.max(0.0f, heightToolSoftness - 0.1f);
				super.keyDown = 0;
			}
		}
		else if (activeTool == 2) // Texture tool controls
		{
			if (super.keyDown == ']')
			{
				textureToolRadius = Math.min(10, textureToolRadius + 1);
				super.keyDown = 0;
			}
			else if (super.keyDown == '[')
			{
				textureToolRadius = Math.max(1, textureToolRadius - 1);
				super.keyDown = 0;
			}
			else if (super.keyDown == 'y' || super.keyDown == 'Y')
			{
				textureToolSoftness = Math.min(1.0f, textureToolSoftness + 0.1f);
				super.keyDown = 0;
			}
			else if (super.keyDown == 'u' || super.keyDown == 'U')
			{
				textureToolSoftness = Math.max(0.0f, textureToolSoftness - 0.1f);
				super.keyDown = 0;
			}
			else if (super.keyDown == 'q' || super.keyDown == 'Q')
			{
				selectedTexture = Math.max(0, selectedTexture - 1);
				super.keyDown = 0;
			}
			else if (super.keyDown == 'e' || super.keyDown == 'E')
			{
				selectedTexture = Math.min(255, selectedTexture + 1);
				super.keyDown = 0;
			}
		}
		else if (activeTool == 3) // Overlay tool controls
		{
			if (super.keyDown == 'q' || super.keyDown == 'Q')
			{
				selectedOverlay = Math.max(0, selectedOverlay - 1);
				super.keyDown = 0;
			}
			else if (super.keyDown == 'e' || super.keyDown == 'E')
			{
				selectedOverlay = Math.min(255, selectedOverlay + 1);
				super.keyDown = 0;
			}
		}
		
		// Global shortcuts
		if (super.keyDown == 's' || super.keyDown == 'S')
		{
			shadowsEnabled = !shadowsEnabled;
			engineHandle.shadowsEnabled = shadowsEnabled; // Pass flag to engine
			updateRender(true); // Refresh rendering with new shadow setting
			super.keyDown = 0;
		}
		else if (super.keyDown == 19) // Ctrl+S
		{
			System.out.println("Saving sectors...");
			boolean success = saveSectors();
			System.out.println("Save " + (success ? "successful" : "failed"));
			super.keyDown = 0;
		}
		else if (super.keyDown == 26) // Ctrl+Z
		{
			performUndo();
			super.keyDown = 0;
		}
	}

	
	// Height painting tool
	private void paintHeight(int centerX, int centerY, boolean raise)
	{
		if (!sectionLoaded) 
		{
			System.out.println("Cannot paint height: section not loaded");
			return;
		}
		
		System.out.println("Height tool: " + (raise ? "raising" : "lowering") + " at " + centerX + ", " + centerY);
		
		int strength = raise ? heightToolStrength : -heightToolStrength;
		
		for (int dx = -heightToolRadius; dx <= heightToolRadius; dx++)
		{
			for (int dy = -heightToolRadius; dy <= heightToolRadius; dy++)
			{
				int x = centerX + dx;
				int y = centerY + dy;
				
				// Calculate distance from center for falloff
				double distance = Math.sqrt(dx * dx + dy * dy);
				if (distance > heightToolRadius) continue;
				
				// Calculate falloff factor based on softness
				double falloff = 1.0;
				if (heightToolSoftness > 0)
				{
					falloff = Math.max(0, 1.0 - (distance / heightToolRadius) * heightToolSoftness);
				}
				
				Tile tile = getTileAtPos(x, y);
				if (tile != null)
				{
					int currentElevation = tile.groundElevation & 0xFF; // Convert byte to unsigned int
					int newElevation = currentElevation + (int)(strength * falloff);
					tile.groundElevation = (byte)Math.max(0, Math.min(255, newElevation));
					System.out.println("Tile at " + x + "," + y + ": " + currentElevation + " -> " + (tile.groundElevation & 0xFF));
					
					// Update the sector
					byte sectorIndex = 0;
					int sectorX = x, sectorY = y;
					
					if (x >= 48 && y < 48)
					{
						sectorIndex = 1;
						sectorX -= 48;
					}
					else if (x < 48 && y >= 48)
					{
						sectorIndex = 2;
						sectorY -= 48;
					}
					else if (x >= 48 && y >= 48)
					{
						sectorIndex = 3;
						sectorX -= 48;
						sectorY -= 48;
					}
					
					engineHandle.sectors[sectorIndex].setTile(sectorX, sectorY, tile);
				}
			}
		}
		// Always update terrain geometry, shadow disable needs to be handled in EngineHandle
		updateRender(true);
		isModified = true;
	}
	
	// Texture painting tool
	private void paintTexture(int centerX, int centerY)
	{
		if (!sectionLoaded)
		{
			System.out.println("Cannot paint texture: section not loaded");
			return;
		}
		
		System.out.println("Texture tool: painting texture " + selectedTexture + " at " + centerX + ", " + centerY);
		
		for (int dx = -textureToolRadius; dx <= textureToolRadius; dx++)
		{
			for (int dy = -textureToolRadius; dy <= textureToolRadius; dy++)
			{
				int x = centerX + dx;
				int y = centerY + dy;
				
				// Check bounds
				if (x >= 0 && x < 96 && y >= 0 && y < 96)
				{
					float distance = (float)Math.sqrt(dx * dx + dy * dy);
					if (distance <= textureToolRadius)
					{
						// Calculate falloff based on distance and softness
						float falloff = 1.0f;
						if (distance > textureToolRadius * textureToolSoftness)
						{
							falloff = 1.0f - ((distance - textureToolRadius * textureToolSoftness) / 
											 (textureToolRadius * (1.0f - textureToolSoftness)));
							falloff = Math.max(0.0f, falloff);
						}
						
						// Apply texture blending based on falloff
						Tile tile = getTileAtPos(x, y);
						if (tile != null)
						{
							// Store original for undo
							Tile originalTile = new Tile();
							originalTile.groundElevation = tile.groundElevation;
							originalTile.groundTexture = tile.groundTexture;
							originalTile.groundOverlay = tile.groundOverlay;
							
							// Blend texture based on falloff strength
							if (falloff > 0.9f)
							{
								// Full strength - set to selected texture
								tile.groundTexture = (byte)selectedTexture;
							}
							else if (falloff > 0.3f)
							{
								// Medium strength - blend with current texture
								int currentTexture = tile.groundTexture & 0xFF;
								int blendedTexture = (int)(currentTexture * (1.0f - falloff) + selectedTexture * falloff);
								tile.groundTexture = (byte)blendedTexture;
							}
							// Low falloff - don't change texture
							
							if (tile.groundTexture != originalTile.groundTexture)
							{
								// Store undo action
								UndoAction action = new UndoAction(x, y, originalTile, tile, "texture");
								addUndoAction(action);
								
								// Update the sector
								byte sectorIndex = 0;
								int sectorX = x, sectorY = y;
								
								if (x >= 48 && y < 48)
								{
									sectorIndex = 1;
									sectorX -= 48;
								}
								else if (x < 48 && y >= 48)
								{
									sectorIndex = 2;
									sectorY -= 48;
								}
								else if (x >= 48 && y >= 48)
								{
									sectorIndex = 3;
									sectorX -= 48;
									sectorY -= 48;
								}
								
								engineHandle.sectors[sectorIndex].setTile(sectorX, sectorY, tile);
							}
						}
					}
				}
			}
		}
		updateRender(true);
		isModified = true;
	}
	
	// Overlay painting tool - single tile only
	private void paintOverlay(int centerX, int centerY)
	{
		if (!sectionLoaded)
		{
			System.out.println("Cannot paint overlay: section not loaded");
			return;
		}
		
		System.out.println("Overlay tool: painting overlay " + selectedOverlay + " at " + centerX + ", " + centerY);
		
		// Only paint the center tile (no radius or softness)
		if (centerX >= 0 && centerX < 96 && centerY >= 0 && centerY < 96)
		{
			Tile tile = getTileAtPos(centerX, centerY);
			if (tile != null)
			{
				tile.groundOverlay = (byte)selectedOverlay;
				
				// Update the sector
				byte sectorIndex = 0;
				int sectorX = centerX, sectorY = centerY;
				
				if (centerX >= 48 && centerY < 48)
				{
					sectorIndex = 1;
					sectorX -= 48;
				}
				else if (centerX < 48 && centerY >= 48)
				{
					sectorIndex = 2;
					sectorY -= 48;
				}
				else if (centerX >= 48 && centerY >= 48)
				{
					sectorIndex = 3;
					sectorX -= 48;
					sectorY -= 48;
				}
				
				engineHandle.sectors[sectorIndex].setTile(sectorX, sectorY, tile);
			}
		}
		updateRender(true);
		isModified = true;
	}
	
	private void drawToolPreview()
	{
		int mouseX = currentMouseX;
		int mouseY = currentMouseY;
		
		if (activeTool == 1) // Height tool
		{
			int radiusPixels = heightToolRadius * 8;
			gameGraphics.method212(mouseX, mouseY, radiusPixels, 0, 0xff0000); // Red circle
			
			if (heightToolSoftness < 1.0f)
			{
				int softRadiusPixels = (int)(radiusPixels * heightToolSoftness);
				gameGraphics.method212(mouseX, mouseY, softRadiusPixels, 0, 0xffff00); // Yellow soft area
			}
		}
		else if (activeTool == 2) // Texture tool
		{
			int radiusPixels = textureToolRadius * 8;
			gameGraphics.method212(mouseX, mouseY, radiusPixels, 0, 0x00ff00); // Green circle
		}
		else if (activeTool == 3) // Overlay tool
		{
			int radiusPixels = 8; // Single tile only (radius = 1)
			gameGraphics.method212(mouseX, mouseY, radiusPixels, 0, 0x0000ff); // Blue circle
		}
		
		// Draw center point for all tools
		gameGraphics.drawBox(mouseX - 2, mouseY - 2, 4, 4, 0xffffff); // White center dot
	}
	
	/**Move the loaded section in a cardinal direction;
	 * 0 - North,
	 * 1 - East,
	 * 2 - South,
	 * 3 - West,
	 * 4 - Up,
	 * 5 - Down
	 * @param direction - the direction to move
	 */
	public void sectionMove(int direction)
	{
		if(!sectionLoaded)
			return;
		
		int newX = sectionX;
		int newY = sectionY;
		int newH = sectionH;
		
		if(direction == 0) //north
		{
			resetMapEnterPos = true;
			if(sectionY > 1)
				newY -= 1;
		}
		else if(direction == 1) //east
		{
			resetMapEnterPos = true;
			if(sectionX > 1)
				newX -= 1;
		}
		else if(direction == 2) //south
		{
			resetMapEnterPos = true;
			if(sectionY < 255)
				newY += 1;
		}
		else if(direction == 3) //west
		{
			resetMapEnterPos = true;
			if(sectionX < 255)
				newX += 1;
		}
		else if(direction == 4) //up
		{
			resetMapEnterPos = false;
			if(sectionH < 2)
				newH += 1;
			else if(sectionH == 3)
				newH = 0;
		}
		else if(direction == 5) //down
		{
			resetMapEnterPos = false;
			if(sectionH < 3 && sectionH > 0)
				newH -= 1;
			else if(sectionH == 0)
				newH = 3;
		}
		String secName = "h" + newH + "x" + newX + "y" + newY;
		reloadSection(secName);
	}
}
