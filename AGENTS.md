# Contributor Quickstart Guide

This document provides a quick guide for contributors to the Min-Editor project, a cross-platform text editor application.

## Repository Layout

The project is a multi-module Gradle project. Here is a brief overview of the key modules:

- `modules/core`: Contains the core business logic of the text editor, such as the data structures for representing files.
- `modules/piecetable`: Implements the piece table data structure for efficient handling of large files.
- `modules/platform`: Provides platform-specific functionalities.
- `modules/ui-base`: Defines the basic classes and interfaces for the user interface, independent of the specific UI toolkit.
- `modules/ui-fx`: Implements the user interface using JavaFX.
- `modules/bootstrap`: Contains the main entry point of the application and handles the startup process.

## General Guidance

- The project is written in Java 25.
- Do not introduce any external library dependencies other than JavaFX.

## Building and Testing

1. Run the tests.

   ```bash
   ./gradlew test
   ```
2. Build the project:

   ```bash
   ./gradlew clean build
   ```
