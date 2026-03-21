import { useState, useEffect } from 'react';

/**
 * Hook customizado para detectar breakpoints de responsividade
 * Segue os breakpoints do Tailwind CSS
 */
export const useMediaQuery = () => {
  const [isMobile, setIsMobile] = useState(false);
  const [isTablet, setIsTablet] = useState(false);
  const [isDesktop, setIsDesktop] = useState(true);
  const [screenWidth, setScreenWidth] = useState(window.innerWidth);

  useEffect(() => {
    const handleResize = () => {
      const width = window.innerWidth;
      setScreenWidth(width);
      
      // Tailwind breakpoints
      setIsMobile(width < 768); // < md
      setIsTablet(width >= 768 && width < 1024); // md to lg
      setIsDesktop(width >= 1024); // >= lg
    };

    // Listener para mudanças de tamanho
    window.addEventListener('resize', handleResize);
    
    // Chamar uma vez no mount
    handleResize();

    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return { isMobile, isTablet, isDesktop, screenWidth };
};

export default useMediaQuery;
