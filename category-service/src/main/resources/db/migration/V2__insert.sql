-- Insert initial data into category
INSERT INTO category (name, type, photo_url, is_public) VALUES
  ('Electronics', 'SWIPE', 'https://res.cloudinary.com/dctiucda1/image/upload/v1760618779/image_a9gqss.png', true),
  ('Books', 'SWIPE', 'https://res.cloudinary.com/dctiucda1/image/upload/v1760618779/image_a9gqss.png', true),
  ('Clothing', 'SWIPE', 'https://res.cloudinary.com/dctiucda1/image/upload/v1760618779/image_a9gqss.png', true);

-- Insert initial data into option
INSERT INTO option (cat_id, name) VALUES
  (1, 'Smartphones'),
  (1, 'Laptops'),
  (2, 'Fiction'),
  (2, 'Non-fiction'),
  (3, 'Men'),
  (3, 'Women');