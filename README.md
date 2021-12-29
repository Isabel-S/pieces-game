# Pieces!
Pieces! is Java desktop game that lets players create, play, save, and share square and triangle cut puzzles. I made this game from Feb 2020 to Jan 2021 as my final Computer Science project for my highschool International Baccalaureate degree. 

My client is a coding school that wanted a game to use for preschool kids to cultivate programming skills (e.g. problem solving) before heading into coding.

In this game, students can customize their puzzle (number of pieces, puzzle cut, image). This allows teachers to run the activity infinitely, without worry of content. Students can also increase difficulty (number of pieces). Unlike other puzzle programs, students can save their progress, save a puzzle, and share it.

## Rationale for Proposed Solution
A **Java program** was selected because it:
<ul>
  <li>Can run on different operating systems; TCS uses Windows, but students bring in their own computers like MacBooks.</li>
  <li>Is object-oriented; code can be reused (e.g. mutliple objects from a single puzzle class)</li>
  <li>Allows for a graphical user interface through the Swing library so the program can be visually interactive</li>
  <li>Can manipulate images, which is useful for creating puzzles, through the AWT library</li>
</ul>

**Demographic:** The current simplest activity, Scratch, is too complex to teach for kids below age 6. Instead, pre-coding activities like puzzles are fun, and a good educational start.

**Skills:** Puzzles develop **problem solving** skills, a key skill for computer programming. Approaching a puzzle uses similar thinking skills (e.g. breaking down a big picture) to that of coding.

**Computer Familiarity:** Drag and drop, and other computer functionalities will prepare students for future applications like Scratch.

## Design and Planning

#### Structure Chart
<img width="700" alt="Structure Chart" src="https://user-images.githubusercontent.com/23027638/147698241-cab4338b-7722-4bd3-91b8-02dff37417ab.png">

#### User Interface Design
<img width="600" alt="User Interface Design" src="https://user-images.githubusercontent.com/23027638/147698300-b7ef53f9-2ca4-4c96-88b9-7b2f6a95b92f.png">

#### Puzzles Class and Puzzle Helper UML Diagrams
<img width="600" alt="UML Diagrams" src="https://user-images.githubusercontent.com/23027638/147698312-ba5718fa-41a5-46eb-aaa1-fd5bd77c8618.png">

## Screenshots and GIFS

#### Puzzle Generation
![puzzle generation](https://user-images.githubusercontent.com/23027638/147698350-642df3c5-4500-4dca-b74c-29570a5125cc.gif)

#### Triangle Cut and and Grid Cut puzzles
<img width="800" alt="Triangle Cut and Grid Cut puzzles" src="https://user-images.githubusercontent.com/23027638/147695391-a4d00ffc-99d5-4615-8af3-5424f42739b9.png">

#### Completing a Puzzle
<img width="400" alt="Completed Puzzle" src="https://user-images.githubusercontent.com/23027638/147699124-56ff3542-f326-45b2-b329-5e6c0bb6221a.png">
Completing a puzzle creates an image congratulating the player. 

#### View Puzzles
![view puzzles](https://user-images.githubusercontent.com/23027638/147697898-336bd7a9-8c19-4d4e-9c17-79b00756486a.gif)

#### Viewing Saved Versions of Puzzles
<img width="500" alt="Saved Versions" src="https://user-images.githubusercontent.com/23027638/147699231-48f4bfc8-f9f2-4d7c-b2a1-d5763d0e2ef0.png">
Each puzzle can have different attempts or "saved versions" of it.

#### Sharing a Puzzle
![share](https://user-images.githubusercontent.com/23027638/147698997-6f7679cb-dde0-4314-9e6b-f6c2baaf90f3.gif)

## Full Video Demo
https://user-images.githubusercontent.com/23027638/147694025-a0cae1ac-4ebb-4a9c-9967-1a1156e20e27.mp4

