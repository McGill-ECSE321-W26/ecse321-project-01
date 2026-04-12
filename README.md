<h1 align="center" style="font-family: 'Lexend Deca'; font-size: xx-large ">Kloth</h1>

<p align="center" style="font-family: 'Lexend Deca'">
<strong>ECSE 321 - Project Group 01</strong> <br>
McGill University, Winter 2026
</p>

![Kloth GIF](docs/kloth-gif-demo.gif)

---

## Introduction
In this project, we will create an online platform for ordering, managing and delivering clothes.

## Full Documentation
See the [Wiki](https://github.com/McGill-ECSE321-W26/ecse321-project-01/wiki) for full documentation.

## Team introduction
We are Team 1. We chose this team number because we are the :1st_place_medal: s. We are all friends and now each other prior to forming this team, which makes it easy for us to communicate and know each other's strengths and weaknesses. Although we are all in Computer Engineering, we have a wide range of skills and knowledge in terms of software engineering and software development. We are excited to work on a big project like this one, since it will help us learn a lot and give us relevant experience.

### Team members
| Name            | GH Username | Team Roles                             |
| --------------- | ----------- | -------------------------------------- |
| Santiago Padron | santipadron | Project Manager, Documentation Manager |
| Dragos Bajanica | Gnardisitor | Software Lead                          |
| Bassam Baki     | B-Baki      | Software Developer (Back-end)          |
| Ethan Tran      | tran-ethan  | Project Manager, Software Lead         |
| David Tang      | Tangdavid1  | Software Developer (Front-end)         |
| Jason Wang      | jayjay4387  | Creative Director, Software Developer  |
| Minh Vo         | MinhVo2005  | Software Developer, Test Engineer      |

## Main scope of the project
Here is the Project Overview given by the client:
> Your favourite local fashion store wants to create an online platform for ordering and delivering clothes. The application is managed by the owner, who gets their own user account. User accounts can also be created for store employees and customers. In addition, customers also need to provide their address for delivery purposes.
> 
> The manager is responsible for managing the product catalog, including adding and updating clothing items available for purchase. Customers can browse items, add items to their shopping cart, and place orders for delivery. At the time of ordering, they must specify a delivery date for their order. The employees are responsible for preparing customer orders for delivery.
> 
> The manager of the platform would also like to add a loyalty program for returning customers, but is unsure of the details of the program (and might need your help deciding on its terms).
> 
> The above description is intentionally poorly defined. In your project teams, you will gather requirements, design a multi-tier software solution to satisfy those requirements, implement the system, and validate that the system is satisfying the requirements.

The goals of the project are as follows:
- Develop a fully functionning application for an online clothing platform
- Learn best practices in Software Engineering
- Learn how to interact with databases and persistence
- Learn Java development tools and frameworks
- Practice working in an Agile team
- Learn industry standard project management practices (SCRUM, Agile, Kanban, GH Issues, etc)
- Learn front-end development with JavaScript

> [!NOTE]
> Our application must support the scenarios described for every stakeholder. All functionality of the system needs to be accessible via the web frontend for respective stakeholders. External systems or services are not required to be integrated.

## Project contribution overview
Following is a table containing an overview of our contributions (in hours) for each deliverable. For information on the roles of every team member, please refer to the [Team Introduction Table](#team-members) up top.

| Deliverable / Task                                  | Santiago | Dragos | Bassam | Ethan | David | Jason | Minh  | Total |
| --------------------------------------------------- | :------: | :----: | :----: | :---: | :---: | :---: | :---: | :---: |
| **Deliverable 1**                                   |   ---    |  ---   |  ---   |  ---  |  ---  |  ---  |  ---  |  ---  |
| Requirements                                        |    2     |   2    |   3    |   2   |   2   |   2   |   2   |  15   |
| UML Domain Model                                    |   0.5    |   2    |   2    |   2   |   1   |   1   |   1   |  9.5  |
| Persistence Layer                                   |          |   1    |        |   1   |   1   |       |   1   |   4   |
| Testing of Persistence Layer                        |          |        |        |       |   1   |   2   |   2   |   5   |
| Build System                                        |          |        |        |   2   |       |       |       |   2   |
| Project Management + Documentation                  |    4     |   1    |   1    |   1   |   1   |   1   |   1   |  10   |
| Team Contract + Success Spectrum                    |    2     |  0.5   |  0.5   |  0.5  |  0.5  |  0.5  |  0.5  |   5   |
| **Deliverable 1 Totals**                            |   8.5    |  6.5   |  6.5   |  8.5  |  6.5  |  6.5  |  7.5  | 50.5  |
| **Deliverable 2**                                   |   ---    |  ---   |  ---   |  ---  |  ---  |  ---  |  ---  |  ---  |
| API implementation (service and controller methods) |    5     |   5    |   5    |   7   |       |       |       |  22   |
| QA plan and report                                  |          |        |        |       |   3   |       |   3   |   6   |
| Unit testing of backend                             |    1     |   2    |   2    |   2   |   5   |   2   |   5   |  19   |
| Integration testing                                 |    1     |   1    |   1    |   1   |   3   |   6   |   3   |  16   |
| Gradle task for integration tests                   |          |        |        |       |       |   2   |       |   2   |
| Project management + documentation                  |    3     |   2    |   1    |   2   |   2   |   1   |   1   |  12   |
| **Deliverable 2 Totals**                            |    10    |   10   |   9    |  12   |  13   |  11   |  12   |  76   |
| **Deliverable 3**                                   |   ---    |  ---   |  ---   |  ---  |  ---  |  ---  |  ---  |  ---  |
| Architecture Modelling                              |          |        |        |       |   3   |       |       |       |
| Web Frontend                                        |    8     |   8    |   8    |   8   |   8   |   8   |   8   |  56   |
| Fixing the backend                                  |          |   3    |        |   3   |       |       |   3   |   9   |
| Project management + documentation                  |    3     |        |        |   1   |       |       |       |   4   |
| **Deliverable 3 Totals**                            |    11    |   11   |   8    |  12   |  11   |   8   |  11   |  72   |
| **Grand Total**                                     |   29.5   |  27.5  |  23.5  | 32.5  | 30.5  | 25.5  | 30.5  | 199.5 |

## Project Deliverable 1 (12%)
For all details regarding this deliverable, please refer to the [Deliverable 1 Wiki Page](https://github.com/McGill-ECSE321-W26/ecse321-project-01/wiki/Project-Deliverable-1).

The report for this deliverable can also be found in the Wiki, right [here](https://github.com/McGill-ECSE321-W26/ecse321-project-01/wiki/Deliverable-1-Project-Management-and-Report).

## Project Deliverable 2 (12%)
For all details regarding this deliverable, please refer to the [Deliverable 2 Wiki Page](https://github.com/McGill-ECSE321-W26/ecse321-project-01/wiki/Project-Deliverable-2).

The report for this deliverable can also be found in the Wiki, right [here](https://github.com/McGill-ECSE321-W26/ecse321-project-01/wiki/Deliverable-2-Project-Management-and-Report).

## Project Deliverable 3 (12%)
For all details regarding this deliverable, please refer to the [Deliverable 3 Wiki Page](https://github.com/McGill-ECSE321-W26/ecse321-project-01/wiki/Project-Deliverable-3).

The report for this deliverable can also be found in the Wiki, right [here](https://github.com/McGill-ECSE321-W26/ecse321-project-01/wiki/Deliverable-3-Project-Management-and-Report).

## Group presentation (4%)
The slides for the group presentation + Demo can be found [here](https://github.com/McGill-ECSE321-W26/ecse321-project-01/blob/main/docs/kloth-demo.pdf).