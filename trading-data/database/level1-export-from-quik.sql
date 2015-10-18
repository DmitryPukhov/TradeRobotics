
-- Trigger function
CREATE OR REPLACE FUNCTION level1_export_from_quik() RETURNS trigger AS $level1_export_from_quik$
DECLARE instrument_id int;
    BEGIN
    	instrument_id := pg_catalog.hashtext(NEW.instrument_code);
	-- Create instrument if does not exist
	IF NOT EXISTS (SELECT 1 FROM instrument WHERE id = instrument_id) 
	THEN
		INSERT INTO instrument(id,name,code) VALUES(instrument_id, NEW.instrument_code,NEW.instrument_code);
	END IF;
	-- Create level1 record
	INSERT INTO level1(instrument_id,date,last_time,last_price,last_size,last_price_delta,bid,bid_size,ask,ask_size)
		SELECT instrument_id,NEW.date,NEW.last_time,NEW.last_price,NEW.last_size,NEW.last_price_delta,NEW.bid,NEW.bid_size,NEW.ask,NEW.ask_size;
 
        RETURN NEW;
    END;
$level1_export_from_quik$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS level1_export_from_quik ON level1_export_from_quik;
-- Trigger
CREATE TRIGGER level1_export_from_quik BEFORE INSERT OR UPDATE ON level1_export_from_quik
    FOR EACH ROW EXECUTE PROCEDURE level1_export_from_quik();