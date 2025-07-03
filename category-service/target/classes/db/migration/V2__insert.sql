-- Insert initial data into category
INSERT INTO category (name, type) VALUES
  ('Electronics', 'SWIPE'),
  ('Books', 'SWIPE'),
  ('Clothing', 'SWIPE');

-- Insert initial data into option
INSERT INTO option (cat_id, name) VALUES
  (1, 'Smartphones'),
  (1, 'Laptops'),
  (2, 'Fiction'),
  (2, 'Non-fiction'),
  (3, 'Men'),
  (3, 'Women');