DO
$$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'category_service'
   ) THEN
      CREATE DATABASE category_service;
END IF;
END
$$;
