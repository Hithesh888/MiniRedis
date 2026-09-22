\# MiniRedis



A lightweight Redis-inspired in-memory database built from scratch in Java.



MiniRedis implements a TCP-based client-server architecture, RESP-style command parsing, thread-safe in-memory storage, key expiration, and Append-Only File (AOF) persistence.



\## Features



\- TCP server running on port `6379`

\- RESP-style request parsing

\- Multiple client connections

\- Fixed thread pool for concurrent clients

\- Thread-safe in-memory key-value storage

\- Key expiration with TTL

\- Persistent data using Append-Only File (AOF)

\- AOF replay during server startup

\- Persistent expiration timestamps

\- Automated test suite

\- Custom Java client for testing



\## Supported Commands



| Command | Description |

|---|---|

| `PING` | Check whether the server is running |

| `PING message` | Return a message |

| `ECHO message` | Echo a message |

| `SET key value` | Store a key-value pair |

| `SET key value EX seconds` | Store a key-value pair with expiration |

| `GET key` | Retrieve a value |

| `DEL key` | Delete a key |

| `EXISTS key` | Check whether a key exists |

| `INCR key` | Increment an integer value |

| `EXPIRE key seconds` | Set expiration on an existing key |

| `TTL key` | Check remaining key lifetime |



\## Architecture



```text

&#x20;                +----------------------+

&#x20;                |      RedisClient     |

&#x20;                +----------+-----------+

&#x20;                           |

&#x20;                           | TCP

&#x20;                           v

&#x20;                +----------------------+

&#x20;                |     MiniRedis Server |

&#x20;                |       Main.java      |

&#x20;                +----------+-----------+

&#x20;                           |

&#x20;                           v

&#x20;                +----------------------+

&#x20;                |     RespParser       |

&#x20;                +----------+-----------+

&#x20;                           |

&#x20;                           v

&#x20;                +----------------------+

&#x20;                |   CommandHandler     |

&#x20;                +----------+-----------+

&#x20;                           |

&#x20;                           v

&#x20;                +----------------------+

&#x20;                |      DataStore       |

&#x20;                | ConcurrentHashMap    |

&#x20;                +----------+-----------+

&#x20;                           |

&#x20;                           v

&#x20;                +----------------------+

&#x20;                |     AOFLogger        |

&#x20;                | appendonly.aof        |

&#x20;                +----------------------+

