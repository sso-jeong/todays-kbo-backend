CREATE TABLE User (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    teamId INT,
    createdAt DATETIME,
    updatedAt DATETIME,
    FOREIGN KEY (teamId) REFERENCES Team(id)
);

CREATE TABLE Team (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(10),
    stadium VARCHAR(50),
    coach VARCHAR(50),
    championships TEXT
);


CREATE TABLE Player (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    number INT,
    position VARCHAR(20),
    teamId INT,
    birthDate DATE,
    imageUrl TEXT,
    FOREIGN KEY (teamId) REFERENCES Team(id)
);

CREATE TABLE PlayerStat (
    id INT PRIMARY KEY AUTO_INCREMENT,
    playerId INT,
    season YEAR,
    battingAverage DECIMAL(4,3),
    HomeRuns INT,
    RBI INT,
    ERA DECIMAL(4,2),
    strikeOuts INT,
    FOREIGN KEY (playerId) REFERENCES Player(id)
);

CREATE TABLE TeamStat (
    id INT PRIMARY KEY AUTO_INCREMENT,
    teamId INT,
    season YEAR,
    wins INT,
    losses INT,
    winRate DECIMAL(4,3),
    recentFiveGames VARCHAR(10),
    FOREIGN KEY (teamId) REFERENCES Team(id)
);

CREATE TABLE GameSchedule (
    id INT PRIMARY KEY AUTO_INCREMENT,
    gameDate DATE,
    startTime TIME,
    stadium VARCHAR(100),
    homeTeamId INT,
    awayTeamId INT,
    broadcastChannel VARCHAR(50),
    status VARCHAR(20),
    FOREIGN KEY (homeTeamId) REFERENCES Team(id),
    FOREIGN KEY (awayTeamId) REFERENCES Team(id)
);

CREATE TABLE Game (
    id INT PRIMARY KEY AUTO_INCREMENT,
    gameDate DATE,
    homeTeamId INT,
    awayTeamId INT,
    homeScore INT,
    awayScore INT,
    status VARCHAR(20),
    inningStatus VARCHAR(20),
    highlightUrl TEXT,
    FOREIGN KEY (homeTeamId) REFERENCES Team(id),
    FOREIGN KEY (awayTeamId) REFERENCES Team(id)
);

CREATE TABLE Highlight (
    id INT PRIMARY KEY AUTO_INCREMENT,
    gameId INT,
    videoUrl VARCHAR(255),
    thumbnailUrl VARCHAR(255),
    title VARCHAR(100),
    description TEXT,
    createdAt DATETIME,
    FOREIGN KEY (gameId) REFERENCES Game(id)
);

CREATE TABLE Post (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT,
    teamId INT,
    title VARCHAR(255),
    content TEXT,
    createdAt DATETIME,
    FOREIGN KEY (userId) REFERENCES User(id),
    FOREIGN KEY (teamId) REFERENCES Team(id)
);

CREATE TABLE Comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    postId INT,
    userId INT,
    content TEXT,
    createdAt DATETIME,
    updatedAt DATETIME,
    teamId INT,
    FOREIGN KEY (postId) REFERENCES Post(id),
    FOREIGN KEY (userId) REFERENCES User(id),
    FOREIGN KEY (teamId) REFERENCES Team(id)
);
