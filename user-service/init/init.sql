DO
$$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'user_service'
   ) THEN
      CREATE DATABASE user_service;
END IF;
END
$$;