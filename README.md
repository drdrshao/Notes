# A1 Notes
George Shao (g3shao 20849675)

## Setup
* Windows 11
* IntelliJ IDEA 2022.2.3 (Community Edition)
* kotlin.jvm 1.7.10
* Java SDK 17.0.2 (temurin)

## Basic
I made a simple change to the Create button because Piazza question @74
- Users cannot create empty notes for both the List and Grid views. But they can use space or blank lines to create blank notes. Only when the user enters the content the create button will change to the standard color, and the notes will be created.

## Enhancement 
I added the following enhancement:
- User can assign importance when adding new notes
1. User must enter importance to create notes; importance can be an integer from 0 to 2147483647.
2. If the user wants to change importance, the user can input the same content but with different importance. Nothing will happen if they input the same content and the same importance.
3. The user can have more than one note with the same importance and different content.

- They can sort notes by importance
1. A choice box with the additional options "Importance (asc)" and "Importance (desc)." "Importance (asc)" arranges importance from the smallest integer number to the largest integer number and "Importance (desc)" from the largest integer number to the smallest integer number.