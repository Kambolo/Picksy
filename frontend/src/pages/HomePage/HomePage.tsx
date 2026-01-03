import Navbar from "../../components/Navbar/Navbar";
import ContactSection from "./ContactSection/ContactSection";
import Footer from "./Footer/Footer";
import HeroSection from "./HeroSection/HeroSection";
import "./HomePage.css";
import HowItWorksSection from "./HowItWorksSection/HowItWorksSection";

function HomePage() {
  return (
    <div className="home-page-container">
      <Navbar />
      <HeroSection />
      <HowItWorksSection />
      <ContactSection />
      <Footer />
    </div>
  );
}

export default HomePage;
