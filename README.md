# Reliable-Data-Transfer-Server-Client

Implements a basic reliable data transfer protocol built on the application layer
but utilizes UDP datagrams. UDP is not a reliable protocol by default, the purpose
of this project was to gain an understanding in how to implement reliability at
the application level

The project utilizes a sender and a receiver. The sender is transferring any file
over to the receiver. By utilizing ACKs and sequence numbers, we can guarantee
reliability by resending packets until the ACK is received for that packet.

Running:
1. Compile the sender and receiver
2. Start the receiver and input the sender's information
3. Run the sender with the arguments: IP, Port, Sender Port, FileName, Timeout
