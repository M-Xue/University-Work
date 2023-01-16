-- COMP3311 22T3 Assignment 1
--
-- Fill in the gaps ("...") below with your code
-- You can add any auxiliary views/function that you like
-- The code in this file *MUST* load into an empty database in one pass
-- It will be tested as follows:
-- createdb test; psql test -f ass1.dump; psql test -f ass1.sql
-- Make sure it can load without error under these conditions

-- Get the max ABV value for beers with the word barrel and aged in their notes
create or replace view max_barrel_aged_abv(abv) as 
select max(b.abv)
from beers b
where lower(b.notes) like '%barrel%' 
    and lower(b.notes) like '%aged%';

-- Get all the ingredients of a given ingredient type of a beer of the given ID concatenated with a ' + ' substring.
create or replace function get_beer_ingredient_of_type(_i_type IngredientType, _beer_id integer) 
    returns text
as $$
declare 
    _ingredients text;
begin

    select string_agg(
        i.name, 
        ',' 
        order by i.name
    ) as ingredients into _ingredients
    from contains c 
        join ingredients i on (c.ingredient=i.id)
    where c.beer=_beer_id 
        and i.itype=_i_type
    group by c.beer;

    return _ingredients;
end;
$$ language plpgsql;

-- Get all the brewers of a beer concatenated with a ' + ' substring (usually will return one brewery but just in case there is a collab beer, it'll concatenate them)
create or replace function get_beer_brewers(_beer_id integer) 
    returns text
as $$
declare 
    _brewers text;
begin

    select string_agg(
        bry.name,
        ' + '
        order by bry.name
    ) as brewers into _brewers
    from beers b 
        join brewed_by bb on (b.id=bb.beer)
        join breweries bry on (bb.brewery=bry.id)
    where b.id = _beer_id
    group by b.id;

    return _brewers;
end;
$$ language plpgsql;

-- Q1: new breweries in Sydney in 2020

create or replace view Q1(brewery,suburb) as 
select b.name as brewery, l.town as suburb 
from locations l 
    join breweries b on (l.id=b.located_in)
where l.metro='Sydney' 
	and b.founded=2020;

-- Q2: beers whose name is same as their style

create or replace view Q2(beer,brewery) as 
select style_beers.beer, get_beer_brewers(style_beers.beer_id) as brewery 
from (
	select b.id as beer_id, b.name as beer 
	from beers b
		join styles s on (b.style=s.id)
	where b.name=s.name
) as style_beers
    join brewed_by bb on (style_beers.beer_id=bb.beer)
    join breweries bry on (bry.id=bb.brewery);

-- Q3: original Californian craft brewery

create or replace view Q3(brewery,founded) as
select b.name as brewery, b.founded 
from breweries b 
    join locations l on (b.located_in=l.id) 
where l.region='California' 
	and b.founded=(
		-- Get the oldest year a brewery was established
		select min(founded) 
		from (
			select b.name as brewery, b.founded 
			from breweries b 
				join locations l on (b.located_in=l.id) 
			where l.region='California' 
		) as brewery_founded_dates
	);

-- Q4: all IPA variations, and how many times each occurs

create or replace view Q4(style,count) as
select s.name as style, count(*) 
from beers b 
    join styles s on (b.style=s.id) 
where s.name like '%IPA%' 
group by s.name 
order by s.name;

-- Q5: all Californian breweries, showing precise location

create or replace view Q5(brewery,location) as
select b.name as brewery,
case
    when l.town is not null then l.town
    when l.metro is not null then l.metro
    when l.region is not null then l.region
    else l.country
end as location
from breweries b 
    join locations l on (b.located_in=l.id)
where l.region='California';

-- Q6: strongest barrel-aged beer

create or replace view Q6(beer,brewery,abv) as 
select distinct b.name as beer, get_beer_brewers(b.id) as brewery, b.abv 
from beers b 
where lower(b.notes) like '%barrel%' 
    and lower(b.notes) like '%aged%' 
    and b.abv=(
        select * 
		from max_barrel_aged_abv
    );

-- Q7: most popular hop

create or replace view Q7(hop) as
select i.name as hop
from ingredients i
where i.id in (
    -- Getting all the IDs of hops that have the same number of uses as the most popular hop
    select hops.id
    from beers b 
        join contains c on (b.id=c.beer) 
        join (
            select * 
			from ingredients 
			where itype='hop'
        ) as hops on (hops.id=c.ingredient) 
    group by hops.id
    having count(*)=(
        -- Getting the number of beers the use the most popular hops
        select max(count)
        from (
            select count(*)
            from beers b 
                join contains c on (b.id=c.beer) 
                join (
                    select * 
					from ingredients 
					where itype='hop'
                ) as hops on (hops.id=c.ingredient) 
            group by hops.id
        )
        as hops_count
    )
);

-- Q8: breweries that don't make IPA or Lager or Stout (any variation thereof)

create or replace view Q8(brewery) as
select bry.name as brewery 
from breweries bry
where bry.id not in (
    select distinct bb.brewery as bid 
    from brewed_by bb 
		join (
			select b.id 
			from beers b 
				join styles s on (s.id=b.style) 
			where b.style in (
				select s.id 
				from styles s 
				where s.name like '%IPA%' 
					or s.name like '%Lager%' 
					or s.name like '%Stout%'
			)
		) as pop_style_beer on (bb.beer=pop_style_beer.id)
);

-- Q9: most commonly used grain in Hazy IPAs

create or replace view Q9(grain) as
select i.name as grain
from ingredients i 
    join (
        -- Attaches all of a beers ingredients IDs to the beer
        select c.ingredient
        from contains c 
			join (
				-- Select all the beers with Hazy IPA style
				select b.id
				from beers b 
				where b.style=(
					-- Getting the ID of the Hazy IPA style
					select s.id
					from styles s
					where s.name='Hazy IPA'
            	)
        	) as bid on (bid.id=c.beer)
    ) as iid on (i.id=iid.ingredient)
where itype='grain'
group by i.name
having count(*)=(
    select max(count) 
	from (
        select i.id, count(*)
        from ingredients i 
            join (
                -- Attaches all of a beer's ingredients IDs to the beer
                select c.ingredient
                from contains c 
					join (
						-- Select all the beers with Hazy IPA style
						select b.id
						from beers b 
						where b.style=(
							-- Getting the ID of the Hazy IPA style
							select s.id
							from styles s
							where s.name='Hazy IPA'
						)
					) as bid on (bid.id=c.beer)
            ) as iid on (i.id=iid.ingredient)
        where itype='grain'
        group by i.id
    ) as grains_count
);

-- Q10: ingredients not used in any beer

create or replace view Q10(unused) as
select i.name as unused
from ingredients i 
where i.id not in (
    select c.ingredient
    from beers b
    join contains c on (b.id=c.beer)
);

-- Q11: min/max abv for a given country

drop type if exists ABVrange cascade;
create type ABVrange as (minABV float, maxABV float);

create or replace function Q11(_country text) 
    returns ABVrange
as $$
declare 
    maxABV float;
    minABV float;
begin

    if _country not in (
		select l.country 
        from locations l
	) then 
        return (0::float,0::float);
    end if;

    select max(beer_locations.abv) into maxABV
    from (
		select *
		from beers b
			join brewed_by bb on (b.id=bb.beer)
			join breweries bry on (bry.id=bb.brewery)
			join locations l on (bry.located_in=l.id)
		where l.country=_country
    ) as beer_locations;

    select min(beer_locations.abv) into minABV
    from (
		select * 
		from beers b
			join brewed_by bb on (b.id=bb.beer)
			join breweries bry on (bry.id=bb.brewery)
			join locations l on (bry.located_in=l.id)
		where l.country=_country
    ) as beer_locations;

    return (minABV::numeric(4,1)::float, maxABV::numeric(4,1)::float);
end;
$$ language plpgsql;

-- Q12: details of beers

drop type if exists BeerData cascade;
create type BeerData as (beer text, brewer text, info text);

create or replace function Q12(partial_name text) 
    returns setof BeerData 
as $$
declare 
    _beer record;
    _res_info_brewers text := '';
    _res_info_hops text := '';
    _res_info_grains text := '';
    _res_info_others text := '';
    _res_info text := '';
begin
    for _beer in 
        select *
        from beers b 
        where lower(b.name) like '%' || lower(partial_name) || '%'
    loop 

        -- * Getting the beers
        select * 
        from get_beer_brewers(_beer.id) 
        into _res_info_brewers;

        -- * Getting the ingredients
        _res_info_hops := get_beer_ingredient_of_type('hop'::IngredientType, _beer.id);
        if (_res_info_hops <> '') then
            _res_info_hops := 'Hops: ' || _res_info_hops;
        end if;

        _res_info_grains := get_beer_ingredient_of_type('grain'::IngredientType, _beer.id);
        if (_res_info_grains <> '') then
            _res_info_grains := 'Grain: ' || _res_info_grains;
        end if;

        _res_info_others := get_beer_ingredient_of_type('adjunct'::IngredientType, _beer.id);
        if (_res_info_others <> '') then
            _res_info_others := 'Extras: ' || _res_info_others;
        end if;

        -- * Concatonating all the ingredient strings 
        if (_res_info_hops <> '') then
            _res_info := _res_info_hops;
        end if;

        if (_res_info_hops <> '' and _res_info_grains <> '') then
            _res_info := _res_info || e'\n';
        end if;

        if (_res_info_grains <> '') then
            _res_info := _res_info || _res_info_grains;
        end if;

        if (_res_info_grains <> '' and _res_info_others <> '') then
            _res_info := _res_info || e'\n';
        end if;

        if (_res_info_others <> '') then
            _res_info := _res_info || _res_info_others;
        end if;

		-- * Returning tuple
        if (_res_info = '') then
            return next (
                _beer.name, 
                _res_info_brewers, 
                null::text
            );
        else 
            return next (
                _beer.name, 
                _res_info_brewers, 
                _res_info
            );
        end if;

        _res_info_brewers := '';
        _res_info_hops := '';
        _res_info_grains := '';
        _res_info_others := '';
        _res_info := '';
    end loop;
end;
$$ language plpgsql;
