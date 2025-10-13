DO
$$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'auth_service'
   ) THEN
      CREATE DATABASE auth_service;
END IF;
END
$$;