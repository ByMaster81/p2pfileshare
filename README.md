#P2P Dosya Paylaşım Uygulaması

Bu proje, yerel bir ağ üzerinde çalışan, merkezi bir sunucuya ihtiyaç duymayan (merkeziyetsiz) bir Peer-to-Peer (P2P) dosya paylaşım uygulamasıdır. Java ve Swing kullanılarak geliştirilmiştir. Kullanıcılar, belirledikleri bir klasördeki dosyaları ağdaki diğer kullanıcılarla paylaşabilir ve ağda paylaşılan dosyaları aratarak kendi bilgisayarlarına indirebilirler.

---

## 🚀 Temel Özellikler

- **Merkeziyetsiz Ağ:** Ağa katılmak için merkezi bir sunucuya gerek yoktur. Peer'lar birbirlerini yerel ağ üzerinden otomatik olarak keşfeder.
- **Kolay Kurulum:** Paylaşılacak ve indirilecek dosyaların kaydedileceği klasörleri kullanıcı arayüzü üzerinden kolayca belirleyebilirsiniz.
- **Dosya Arama:** Ağa bağlı diğer kullanıcıların paylaştığı tüm dosyaları tek bir tıklama ile listeleyebilirsiniz.
- **Hızlı İndirme:** Listelenen dosyalara çift tıklayarak indirme işlemini başlatabilirsiniz.
- **İndirme Takibi:** "Downloading files" bölümünden anlık olarak indirilen dosyanın ilerlemesini takip edebilirsiniz.
- **Dosya ve Klasör Hariç Tutma:** Paylaşım klasörünüzde yer almasını istemediğiniz dosya ve klasörleri kolayca filtreleyebilirsiniz.
- **Manuel Dosya Gönderimi:** Ağdaki herhangi bir kullanıcıya, IP adresini girerek doğrudan dosya gönderebilirsiniz.
- **Otomatik Ağ Taraması:** Uygulama, periyodik olarak ağa yayın yaparak yeni peer'ları ve dosyaları otomatik olarak bulur.

---

## 🛠️ Nasıl Çalışır?

Uygulama, hibrit bir P2P ağ mimarisi kullanır:

1. **Peer Keşfi (Discovery):**  
   Bir kullanıcı "Connect" butonuna tıkladığında, uygulama yerel ağa bir UDP broadcast paketi gönderir. Bu paket, "Ben ağdayım ve bu dosyaları paylaşıyorum" mesajını içerir. Ağdaki diğer tüm peer'lar bu mesajı dinler ve gönderen peer'ı kendi "bağlı kullanıcılar" listesine ekler. Yeni katılan peer, ağdaki diğer peer'lardan da benzer bilgileri alarak kendi listesini oluşturur.

2. **Dosya Listeleme:**  
   "Search" butonuna tıklandığında, uygulama kendi bildiği tüm peer'ların paylaştığı dosyaların bir listesini gösterir.

3. **Dosya Transferi:**  
   Bir kullanıcı dosyayı indirmek istediğinde, dosyanın sahibine UDP üzerinden bir istek gönderir. Dosya transferi ise TCP/IP üzerinden gerçekleştirilir. Dosya, 256KB'lık parçalara ayrılır ve alıcı her parçayı aldığında bir onay (ACK) mesajı yollar. Bu, dosya bütünlüğünü sağlar.

---

## 📋 Nasıl Kullanılır?

1. **Uygulamayı Çalıştırın:**  
   Projeyi derleyip `Main.java` dosyasını çalıştırın.

2. **Klasörleri Ayarlayın:**  
   - **Shared Folder:** Paylaşmak istediğiniz dosyaların bulunduğu klasörün yolunu girin ve "Set" butonuna tıklayın.  
   - **Destination Folder:** İndirilen dosyaların kaydedileceği klasörün yolunu girin ve "Set" butonuna tıklayın.

3. **Ağa Bağlanın:**  
   Menüden `Files -> Connect` seçeneğine tıklayarak ağa dahil olun.

4. **Dosyaları Arayın ve İndirin:**  
   - "Search" butonuna tıklayarak ağda paylaşılan tüm dosyaları "Found files" listesinde görün.  
   - İndirmek istediğiniz dosyanın üzerine çift tıklayarak indirme işlemini başlatın.  
   - İndirme durumunu "Downloading files" bölümünden takip edebilirsiniz.

5. **Ağdan Ayrılın:**  
   İşiniz bittiğinde menüden `Files -> Disconnect` seçeneği ile ağ bağlantısını güvenli bir şekilde sonlandırın.

---

## 🔧 Geliştirme

Bu proje Rüştü Yemenici tarafından geliştirilmiştir.  

