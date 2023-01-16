-- COMP3311 21T3 Ass2 ... extra database definitions
-- add any views or functions you need into this file
-- note: it must load without error into a freshly created Movies database
-- you must submit this file even if you add nothing to it

create or replace view peopleMovieJob(personID ,name, movieTitle, job) as 
select p.id as personID, p.name, m.title as movieTitle, pp.job 
from People p
    join Principals pp on (p.id=pp.person)
    join Movies m on (pp.movie=m.id)
order by p.id;




