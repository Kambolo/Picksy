DO
$$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'decision_service'
   ) THEN
      CREATE DATABASE decision_service;
END IF;
END
$$;