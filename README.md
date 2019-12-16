# Player
**Player** is a set of modules allowing players to send messages to each other.

It is implemented in the way to fit the following use case
* There are two players
* One of the players sends a message to the other player
* When a player receives a message, it sends a new message that contains
the received message concatenated with the message counter that the player has
back
* The program finalizes gracefully after the initiator player sent and
received back 10 messages
* Both players run in the same java process (see thread-version module)
* Every player runs in a separate java process (see socket-client and message-broker modules)

To run the program in the mode when both players run in the same process, use `bash thread-player.sh` command.
To run the program in the mode when players run in different processes, use the following instructions.
Firstly, start Message Broker by using the command `bash message-broker.sh`.
Then start an active player using `bash socket-player-active.sh` and a passive player using 
`bash socket-player-passive.sh`.