# Adfectus

Summary:

Android application with machine learning capablities.
First, users will be asked to create an account or sign in to their account managed by Firebase Authentication API.
Then, users can take a picture or pick one from the gallery.
Next, pictures taken or selected will be save in Firebase Storage exclusive for the user account.
After, a link of the picture is created, it will be send to the server (A Flask application hosted on AWS EC2),
which will query Amazon Rekognition API for Emotions, Age, and Gender.
The server will then send the data to the user where it will be saved in a cardView inside a recyclerView.

Android app Features:
* Firebase APIs Intergation. (Authentication and Storage API.)
* Update users login data. (username, email, password.)
* Recyclerview that expands with users data and swipe to delete cards.
* RESTful API using Volley API to communicate with the server.

Server Features:
* Create a Flask application to handle client-server communication
* Setup an instance on AWS EC2
* Use Gunicorn3 and nginx to deploy the flask application
* Integrate AWS Rekognition API.