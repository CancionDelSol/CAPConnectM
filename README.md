# CAPConnectM
# Roger Johnson & Justin Odel

Building and Running:
    - A makefile is provided to build and/or run the program. 
    - The default target is to create a jar called ConnectM.jar in the parent directory
    - Command line arguments for the jar are as follows:
        n m playerOneFirst [address port]

    - The standard game consists of a human and computer player
    - Providing the optional address and port arguments will start a game
        with a computer player and UDP player. The UDP player will handle 
        marshalling moves between it and another program on the other end
        of the address.
    - During program execution, requests for input from the human player will
        be made. The player shoudl enter a valid move from 0 to n-1. Incorrect
        input will be rejected and the user will be prompted again.