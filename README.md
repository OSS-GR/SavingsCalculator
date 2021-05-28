# SavingsCalculator
An Android App that allows you to create, save and edit multiple savings plans.

- # Description
  - In each individual plan you can calculate the total amount of money in your savings account with the added compounding interest after a certain number of years.  
  - You can enter your desired interest compounding period, including continuous compounding(enter 0 in the compounding period field to perform continuous coumpounding).  
  - You can adjust the outcome for inflation, if desired, by providing the current inflation rate.  
  - You can also export the savings plans and their contents into a CSV file that will be located in the Documents folder of your device.  
  - 
## Upcoming Features:  
* Add option to take periodical deposits into account when calculating future savings plan balance.
* Switch from a CVS-based storage system for data structures to a SQL Database.
* Create option for Searching for plans.
* Expand on exporting options,i.e. Cloud,Email,Messaging Apps.
* Add a settings option in the action bar:
  * Add an "About", "Credits" & "Settings" page.
  * Allow disabling GIF Splash Screen and using Static Splash Screen.
   
# Looks & Feel:
**1. A GIF displays the first time you boot the app, greeting you:**  
<img src=/gifs/Splash-Screen-Showcase.gif alt="Splash Screen" width="300">

**2. The main activity is a Recycler View that contains the swipe items, an add button to add plans to the Recycler View and an export button to save all your plans in your Documents folder:**  
<img src=/gifs/Main-Activity.gif alt="Main Activity" width="300">
  
**3. Selecting "Add" displays the following dialog:**  
<img src=/gifs/Main-Activity-Add.gif alt="Main Activity Add" width="300">
  
**4. Selecting "Export" requests permission if it is the first time doing it. When exporting is done a message will display at the bottom of the screen:**  
<img src=/gifs/Main-Activity-Export.gif alt="Main Activity Export" width="300">
  
**5. You can swipe to reveal edit and delete:**  
<img src=/gifs/Main-Activity-Delete.gif alt="Main Activity Delete" width="300">
  
**6. Selecting edit allows you to access the plan's calculator activity:**  
<img src=/gifs/Main-Activity-Edit.gif alt="Main Activity Edit" width="300">

**7. Once in the Calculator Activity, you can fill in the required fields and calculate your future savings account balance:**  
<img src=/gifs/Calculator-Activity.gif alt="Calculator Activity" width="300">


## __Open-Source 3rd Party Libraries Used:__

* **Glide** - An image loading and caching library for Android focused on smooth scrolling.  
* **OpenCSV** - An easy-to-use CSV parser library for Java.  
* **SwipeRevealLayout** - Easy, flexible and powerful Swipe Layout for Android.  

## __Application Icon Used:__  
> ![App Icon](app-icon.png)  
> <div>Icons made by <a href="https://smashicons.com/" title="Smashicons">Smashicons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
