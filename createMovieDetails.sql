create table movieDetails (
    movieId number(9) primary key,
    movieName varchar2(50) unique,
    movieType varchar2(50),
    language varchar2(50),
    releaseDate date,
    casting varchar2(500),
    rating number(4,3),
    totalBusinessDone number(20,3)
);
