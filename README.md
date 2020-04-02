
# Carv Rx code text

This repo is a simple code test. Its demonstrates how we built the carv android application, testing
that you can understand and write applications using the same style.

# Overview

The splash library is a port of the framework that is included in the Carv application. It creates 
a redux based architecture for building android apps. 

The android app is a basic demonstration of how to build a login screen using this library, 
you should use this as reference to gain a superficial understanding of how this approach works. 

Your task is to write the business logic for the `SecretCaveActivity` by filling out the 
`SecretCaveEpic` class, the `secretCavReducer` class. You will only need to edit other classes 
to complete the bonus section 

The SecretCaveActivity listens for the secret code that opens Aladins cave. The desired behaviour is 
  - the secret code is 7 letters long and consists of just the letters A and B.
  - the code is abbabba
 
The SecretCaveActivity should:
 - Allow the user enters to enter the code by tabbing the A button and the B buttons.
 - Every 7 taps a request is sent to the server to verify the secret code ( see `ApiRequests`)
 - While the server request is in progress the buttons are disabled.
 - A progress bar is shown while the server request is in flight.
 - When the code is correct a green tick is shown on the app.

Your task is to fill in the Business logic that fulfills the above behaviour of the app.
 
Bonus:

add an extra button C and change the code to "ABCABC".
Add a clear button that clears all entered text.

Bonus bonus:
Write a test for the `secretCavReducer`