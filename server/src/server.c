#include <netdb.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <pthread.h>
#include <adc_driver.h>

#define MAX 1024
#define PORT 2335
#define SA struct sockaddr

#define SHUTDOWN -1

void write_data(char *buff, int value)
{
	sprintf(buff, "DATA=%d\n", value);
}

void handle_channel(char *req_buff, char *resp_buff)
{
	if (strcmp(req_buff, "CHAN0;") == 0)
	{
		printf("read ch0\n");
		write_data(resp_buff, read_channel(0));
	}
	else if (strcmp(req_buff, "CHAN1;") == 0)
	{
		printf("read ch1\n");
		write_data(resp_buff, read_channel(1));
	}
	else if (strcmp(req_buff, "CHAN2;") == 0)
	{
		printf("read ch2\n");
		write_data(resp_buff, read_channel(2));
	}
	else if (strcmp(req_buff, "CHAN3;") == 0)
	{
		printf("read ch3\n");
		write_data(resp_buff, read_channel(3));
	}
	else if (strcmp(req_buff, "CHAN4;") == 0)
	{
		printf("read ch4\n");
		write_data(resp_buff, read_channel(4));
	}
	else if (strcmp(req_buff, "CHAN5;") == 0)
	{
		printf("read ch5\n");
		write_data(resp_buff, read_channel(5));
	}
	else if (strcmp(req_buff, "CHAN6;") == 0)
	{
		printf("read ch6\n");
		write_data(resp_buff, read_channel(6));
	}
	else if (strcmp(req_buff, "CHAN7;") == 0)
	{
		printf("read ch7\n");
		write_data(resp_buff, read_channel(7));
	}
	else
	{
		printf("Unknown channel\n");
		sprintf(resp_buff, "UNKNOWN CHANNEL;\n");
	}
}

// Function designed for chat between client and server.
int handle(int sockfd)
{
	char buff[MAX];
	char clBuff[MAX];
	int msg_len;
	int m, n;

	// infinite loop for chat
	for (;;)
	{
		bzero(buff, MAX);

		// read the message from client and copy it in buffer
		m = recv(sockfd, buff, MAX - 1, 0);

		if (m == 0)
		{
			printf("Disconnected\n");
			break;
		}
		if (m == -1)
		{
			printf("Error reading client\n");
			break;
		}

		printf("readed %d bytes\n", m);
		// print buffer which contains the client contents
		printf("Client: %s\n", buff);

		// trim trailing \n
		buff[m - 1] = '\0';
		msg_len = m - 1;

		printf("Trimmed Request: %s\n", buff);

		bzero(clBuff, MAX);

		// switch command
		if (buff[msg_len - 1] != ';')
		{
			sprintf(clBuff, "MALFORMED COMMAND;\n");
		}
		else
		{
			if (strstr(buff, "CHAN") != NULL) // handle requests related to channels
			{
				handle_channel(buff, clBuff);
			}
			else if (strcmp(buff, "EXIT;") == 0) // handle exit
			{
				close(sockfd);
				break;
			}
			else if (strcmp(buff, "SHUTDOWN;") == 0) // handle shutdown
			{
				close(sockfd);
				return -1;
			}
			else // unknown command
			{
				printf("unknown command\n");
				sprintf(clBuff, "UNKNOWN COMMAND;\n");
			}
		}

		// and send that buffer to client
		n = send(sockfd, clBuff, strlen(clBuff), 0);

		printf("written %d bytes\n", n);
		if (n == -1)
		{
			printf("Error writing client\n");
			break;
		}
	}

	return 0;
}

// Driver function
int main()
{
	int sockfd, connfd, len;
	struct sockaddr_in servaddr, cli;

	lp_init(0);

	// socket create and verification
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd == -1)
	{
		printf("socket creation failed...\n");
		exit(0);
	}
	else
		printf("Socket successfully created..\n");
	bzero(&servaddr, sizeof(servaddr));

	// assign IP, PORT
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	servaddr.sin_port = htons(PORT);

	// Binding newly created socket to given IP and verification
	if ((bind(sockfd, (SA *)&servaddr, sizeof(servaddr))) != 0)
	{
		printf("socket bind failed...\n");
		exit(0);
	}
	else
		printf("Socket successfully binded..\n");

	// Now server is ready to listen and verification
	if ((listen(sockfd, 5)) != 0)
	{
		printf("Listen failed...\n");
		exit(0);
	}
	else
		printf("Server listening..\n");
	len = sizeof(cli);

	// Accept the data packet from client and verification
	int result;
	while (1)
	{
		connfd = accept(sockfd, (SA *)&cli, &len);
		if (connfd < 0)
		{
			printf("server acccept failed...\n");
			continue;
		}
		else
			printf("server acccept the client...\n");

		// Function for chatting between client and server
		if(handle(connfd) == SHUTDOWN) break;
	}

	// After chatting close the socket
	close(sockfd);
	lp_restore();
	return 0;
}
