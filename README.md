LibrasProject
=============
To use the LibrasProject you need to have the ListeningServer and GestureUI running as well as have created a postgresql database based on the LibrasProject/librasProject/database.backup file.  

You must then set the values on the LibrasProject/librasProject/librasproject/lessonResources/config.properties accordingly.  
The server and port values are related to the ListeningServer.   

**These values must be the same for the GestureUI NetworkConfig tab.**
    
ListeningServer
==============
To start the ListeningServer you must run the .jar file with the port number you wish to listen to.

GestureUI
=========
The GestureUI needs a couple of  steps to be ready for use:  
  * Pre-requisites  
    - Ubuntu 12.04 LTS amd64  
    - Synaptic Package Manager (sudo apt-get install synaptic)  

  * Using Synaptic  
    - Search for 'opencv' and select everything except: opecv-doc, python-opencv, libopencv-gpu-dev (this will deselect libopencv-dev)  
    - Search for 'wxwidgets' (2.8) and select: libwxbase, libwxgtk (for both select also the dev option) wx-common, wx2.8-headers  
    - Search for 'boost' and select: libboost-all-dev (1.48)  

  * Download   
    - Get all the files necessary for this process is in the GestureUI-Installation folder.  

  * Kinect  
    - After connecting the kinect type on the terminal:  
        lsmod | grep kinect  
        sudo modprobe -r gspca_kinect (in case gspca_kinect was shown with the grep)  
        sudo echo "blacklist gspca_kinect" >> /etc/modprobe,d/blacklist.conf  

  * Openni  
    - Unzip openni tar (create a folder)  
    - Navigate to the folder and execute sudo ./install.sh  

  * Sensor  
    - Unzip sensor tar (create a folder)  
    - Navigate to the folder and execute sudo ./install.sh  

  * Terminal  
    - Execute:  
         sudo apt-get install libusb-1.0.0-dev  
         sudo apt-get install freeglut3  

  * NITE  
    - Unzip nite tar (create a folder)  
    - Navigate to the folder and execute sudo ./install.sh  

  * Extract the models on your home folder  
 
  * Extract the gestureUI folder on a folder of choice.  
  
------

Once you start the GestureUI software you should:  
  + Click on the Kinect icon.  
  + Click on the play button.  
  + Fill the server and port values on the NetworkConfig Tab.  
  + With the ListeningServer running click on "Connect to Gesture Interface" icon  
  + Since only two sets of gestures have been trained for the LibrasProject you must select for:  
    - Vogais(Vowels): go to "Gesture Recognition" tab and select "MLP_VivenciaAEIOU_5V_6_MLP" from Configuration>>Trained Model  
    - Consoantes(Consonants): go to "Gesture Recognition" tab and select "MLP_VivenciaBCFLV_2_MLP" from Configuration>>Trained Model  
  + Click on the "Start Gesture Recognition" icon  

Whenever the user wishes to change lesson (From "vogais" to "consoantes" or vice-versa) it is necessary to stop the gesture recognition and change the Trained Model. Then you must restart the recognition and can start the lesson.
