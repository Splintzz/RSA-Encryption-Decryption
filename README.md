# RSA-Encryption-Decryption

Start-Up:
1) Open the command line
2) cd into the source code folder
3) run “java *.java” on the command line
4) run “java Main” on the command line
5) If you are having trouble with the GUI, below is a guide to running through the encryption and decryption process

GUI Helper:

RUNNING THROUGH THE ENCRYPTION AND DECRYPTION PROCESS

Generate Keys:
1) Choose a file or directory to store the keys in by clicking the “Choose Key File Path” button
2) Click the “Generate Key Pairs” button

Side Notes:
- If the file exists, the keys data will write over the chosen file
- If a directory is chosen, the file “keys.txt” will be generated in the folder

Encrypt File:
1) Start by clicking the “Encrypt” radio button
2) Click the “Choose Destination File Path” button and select a directory
3) Click the “Choose Source File Path” and select a file
4) At this point, the “Encrypt” button should be enabled, click that button 

Side Notes:
- When you choose a directory for the destination path, the file “dst.enc” will be generated in that directory
- If the “dst.enc” already exists, the file will be written over
- If the chosen source file is more than 128 bytes, and error will be displayed when the encryption is run
- If an the encryption is successful or an error is displayed, the source and destination paths will be reset

Decrypt File
1) Start by clicking the “Decrypt” radio button
2) Click the “Choose Destination File Path” button and select a directory
3) Click the “Choose Source File Path” and select a file
4) At this point, the “Decrypt” button should be enabled, click that button

Side Notes
- When you choose a directory for the destination path, the file “dst.txt” will be generated in that directory 
- If the “dst.txt” already exists, the file will be written over
- If the encryption process was not run before the decryption, or there was a previous error, and error stating that the encryption process was reset, meaning you must encrypt the file again
- If an the decryption is successful or an error is displayed, the source and destination paths will be reset
