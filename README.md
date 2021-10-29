Từ điển Anh Việt viết bằng Javafx

TODO:
- ~~style.css~~
- ~~giao dien co ban: thanh search, cot results, webview meaning~~
- ~~class model cho database:~~
  + ~~xem nghia cua tu~~ 
  + ~~them, sua, xoa tu moi~~
- ~~chuyen data to databaseModel sang o search (qua controller?)~~
- ~~word suggestion khi dang type~~
- ~~class model goi api cua google, connect voi webview~~
- ~~responsive du chi la 1 it (low prio)~~
- ~~view (scene) cho phan them sua xoa (1 hay nhieu scene), link voi model~~
- ~~sort user created word (created date, last-modified date, alphabet) each has ASC and DESC (default la theo last modified)~~
- ~~add-view voi preview webview?~~
- ~~sua lai sort word trong main-view~~
- ~~" and ' sql error~~

Tính năng: 
- Cơ bản:
  + Tra từ, phát âm từ
  + Thêm, sửa, xóa từ mới
  + Đánh dấu từ yêu thích và từ do người dùng tạo
- Khác:
  + Dùng API của Google Translate (Google Script)
  + Dùng sqlite làm database cho từ điển gốc, từ của người dùng (filter từ trùng lặp trong database)
  + Gợi ý từ khi gõ
  + Giao diện đồ họa cơ bản
  + Giao diện đồ họa dễ dùng và thân thiện: responsive, có preview khi thêm sửa xóa, có class để chuyển dữ liệu giữa các scene