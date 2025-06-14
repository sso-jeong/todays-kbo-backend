

CREATE TABLE game (
    gameId VARCHAR(50) PRIMARY KEY,
    categoryId VARCHAR(50),
    categoryName VARCHAR(50),
    gameDate DATE,
    gameDateTime DATETIME,
    stadium VARCHAR(50),

    homeTeamCode VARCHAR(10),
    homeTeamName VARCHAR(20),
    homeTeamScore INT,

    awayTeamCode VARCHAR(10),
    awayTeamName VARCHAR(20),
    awayTeamScore INT,

    winner VARCHAR(10),
    statusCode VARCHAR(20),
    statusInfo VARCHAR(50),

    cancel BOOLEAN,
    suspended BOOLEAN,
    hasVideo BOOLEAN,
    gameOnAir BOOLEAN,

    roundCode VARCHAR(20),
    reversedHomeAway BOOLEAN,
    homeStarterName VARCHAR(50),
    awayStarterName VARCHAR(50),

    winPitcherName VARCHAR(50),
    losePitcherName VARCHAR(50),
    homeCurrentPitcherName VARCHAR(50),
    awayCurrentPitcherName VARCHAR(50),

    stadiumBroadcastChannel VARCHAR(50),
    weekday VARCHAR(10),
    statusLabel VARCHAR(20)
);

CREATE TABLE InningScore (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gameId VARCHAR(20),
    inning INT,
    homeScore INT,
    awayScore INT

);
CREATE TABLE gameRheb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gameId VARCHAR(20),
    teamCode VARCHAR(10),
    isHome boolean,
    run INT,
    hit INT,
    error INT,
    baseOnBall INT
);