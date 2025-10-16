-- Insert initial data into category
INSERT INTO category (name, type, photo_url) VALUES
  ('Electronics', 'SWIPE', 'https://res.cloudinary.com/dctiucda1/image/upload/v1760618779/image_a9gqss.png'),
  ('Books', 'SWIPE', 'https://res.cloudinary.com/dctiucda1/image/upload/v1760618779/image_a9gqss.png'),
  ('Clothing', 'SWIPE', 'https://res.cloudinary.com/dctiucda1/image/upload/v1760618779/image_a9gqss.png');

-- Insert initial data into option
INSERT INTO option (cat_id, name) VALUES
  (1, 'Smartphones'),
  (1, 'Laptops'),
  (2, 'Fiction'),
  (2, 'Non-fiction'),
  (3, 'Men'),
  (3, 'Women');