DO
$$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'room_service'
   ) THEN
      CREATE DATABASE room_service;
END IF;
END
$$;