# 3D Landscape Editor - Agent Guide

## Build/Test Commands
- **Build**: `ant compile` (in MapEditor directory) - Compiles Java sources and creates JAR
- **Run**: `ant runclient` or `java -jar MapEditor.jar` - Launch the map editor
- **No unit tests**: Project doesn't have automated test suite
- **AreaEditor**: Pre-built executable `RSCAreaEditor.exe`

## Architecture 
- **Type**: Java desktop application for RSC (RuneScape Classic) 3D map editing
- **Structure**: Two subprojects - MapEditor (Java/Ant) and AreaEditor (pre-built executable)
- **Main entry**: `org.rscangel.client.mudclient` class in MapEditor
- **Core components**: 3D rendering engine, sector-based world loading, terrain modification tools
- **Data**: Works with Custom_Landscape.orsc files, compressed archive format for game world data
- **Dependencies**: Uses Apache Ant, requires Java 1.5+, includes libs (mina.jar, xpp3.jar, etc.)

## Code Style Guidelines
- **Package**: `org.rscangel.client.*` hierarchy
- **Classes**: PascalCase (GameFrame, TileEditFrame)
- **Methods/Variables**: camelCase (showSelectSectorsFrame, gameGraphics)
- **Constants**: SCREAMING_SNAKE_CASE (SPRITE_MEDIA_START)
- **Imports**: Java standard library first, then third-party, then local
- **Indentation**: Tab-based (4 spaces equivalent)
- **Braces**: Opening brace on same line
- **Comments**: Use // for single-line, /** */ for JavaDoc
- **Legacy patterns**: Some Hungarian notation remnants (aGraphics936, anInt826)
