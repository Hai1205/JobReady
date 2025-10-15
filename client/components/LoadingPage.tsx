import React, { useState, useEffect } from 'react';

export default function LoadingPage() {
  const [progress, setProgress] = useState(0);
  const [glowIntensity, setGlowIntensity] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setProgress(prev => {
        if (prev >= 100) {
          clearInterval(timer);
          return 100;
        }
        return prev + 1;
      });
    }, 30);

    const glowTimer = setInterval(() => {
      setGlowIntensity(prev => (prev + 1) % 100);
    }, 30);

    return () => {
      clearInterval(timer);
      clearInterval(glowTimer);
    };
  }, []);

  return (
    <div className="min-h-screen bg-black flex items-center justify-center relative overflow-hidden">
      {/* Radial lines from center */}
      <div className="absolute inset-0 flex items-center justify-center">
        {[...Array(36)].map((_, i) => (
          <div
            key={i}
            className="absolute w-1 bg-white origin-bottom"
            style={{
              height: `${40 + (progress / 2)}%`,
              transform: `rotate(${i * 10}deg)`,
              opacity: 0.03,
              transition: 'height 0.3s ease-out'
            }}
          />
        ))}
      </div>

      {/* Main content */}
      <div className="relative z-10 flex flex-col items-center">
        {/* Large HN. logo */}
        <div className="mb-20 relative">
          <div className="flex items-baseline gap-1">
            {/* H */}
            <div 
              className="text-9xl font-black text-white relative transition-all duration-700"
              style={{
                textShadow: `0 0 ${20 + glowIntensity/5}px rgba(255,255,255,0.5)`,
                transform: `translateY(${Math.sin(progress * 0.05) * 2}px)`
              }}
            >
              H
            </div>
            
            {/* N */}
            <div 
              className="text-9xl font-black text-white relative transition-all duration-700"
              style={{
                textShadow: `0 0 ${20 + glowIntensity/5}px rgba(255,255,255,0.5)`,
                transform: `translateY(${Math.sin(progress * 0.05 + 1) * 2}px)`
              }}
            >
              N
            </div>

            {/* Dot */}
            <div 
              className="relative"
              style={{
                transform: `translateY(${Math.sin(progress * 0.05 + 2) * 2}px)`
              }}
            >
              <div 
                className="w-6 h-6 rounded-full bg-white transition-all duration-300"
                style={{
                  boxShadow: `0 0 ${30 + glowIntensity/3}px rgba(255,255,255,0.8)`,
                  transform: `scale(${1 + Math.sin(glowIntensity * 0.1) * 0.2})`
                }}
              />
            </div>
          </div>

          {/* Underline that grows */}
          <div className="mt-6 h-1 bg-white relative overflow-hidden" style={{ width: '100%' }}>
            <div 
              className="absolute left-0 top-0 h-full bg-white"
              style={{
                width: `${progress}%`,
                boxShadow: '0 0 20px rgba(255,255,255,0.8)',
                transition: 'width 0.3s ease-out'
              }}
            />
          </div>
        </div>

        {/* Circular ring progress */}
        <div className="relative w-56 h-56 mb-12">
          {/* Outer ring */}
          <svg className="w-full h-full transform -rotate-90">
            <circle
              cx="112"
              cy="112"
              r="100"
              fill="none"
              stroke="rgba(255,255,255,0.1)"
              strokeWidth="2"
            />
            <circle
              cx="112"
              cy="112"
              r="100"
              fill="none"
              stroke="white"
              strokeWidth="2"
              strokeDasharray={`${2 * Math.PI * 100}`}
              strokeDashoffset={`${2 * Math.PI * 100 * (1 - progress / 100)}`}
              strokeLinecap="round"
              className="transition-all duration-300"
              style={{
                filter: 'drop-shadow(0 0 10px rgba(255,255,255,0.8))'
              }}
            />
          </svg>

          {/* Inner ring */}
          <svg className="absolute inset-4 w-48 h-48 transform rotate-90">
            <circle
              cx="96"
              cy="96"
              r="80"
              fill="none"
              stroke="rgba(255,255,255,0.1)"
              strokeWidth="1"
            />
            <circle
              cx="96"
              cy="96"
              r="80"
              fill="none"
              stroke="white"
              strokeWidth="1"
              strokeDasharray={`${2 * Math.PI * 80}`}
              strokeDashoffset={`${2 * Math.PI * 80 * (1 - progress / 100)}`}
              strokeLinecap="round"
              className="transition-all duration-300"
              style={{
                filter: 'drop-shadow(0 0 8px rgba(255,255,255,0.6))'
              }}
            />
          </svg>

          {/* Center percentage */}
          <div className="absolute inset-0 flex flex-col items-center justify-center">
            <div className="text-6xl font-black text-white mb-2" style={{
              textShadow: '0 0 20px rgba(255,255,255,0.5)'
            }}>
              {progress}
            </div>
            <div className="text-sm text-white tracking-widest opacity-60">PERCENT</div>
          </div>
        </div>

        {/* Loading text with dots */}
        <div className="flex items-center gap-3 mb-8">
          <div className="text-white text-xl font-medium tracking-wider">LOADING</div>
          <div className="flex gap-2">
            {[0, 1, 2].map((i) => (
              <div
                key={i}
                className="w-2 h-2 rounded-full bg-white transition-all duration-300"
                style={{
                  opacity: (glowIntensity + i * 33) % 100 < 33 ? 1 : 0.3,
                  transform: (glowIntensity + i * 33) % 100 < 33 ? 'scale(1.3)' : 'scale(1)',
                  boxShadow: (glowIntensity + i * 33) % 100 < 33 ? '0 0 10px rgba(255,255,255,0.8)' : 'none'
                }}
              />
            ))}
          </div>
        </div>

        {/* Status bar */}
        <div className="w-96 flex items-center gap-4">
          <div className="flex-1 h-px bg-white opacity-20"></div>
          <div className="text-white text-xs font-mono tracking-wider opacity-60">
            {progress < 25 && 'INITIALIZING SYSTEM'}
            {progress >= 25 && progress < 50 && 'LOADING RESOURCES'}
            {progress >= 50 && progress < 75 && 'CONFIGURING'}
            {progress >= 75 && progress < 100 && 'ALMOST READY'}
            {progress === 100 && 'COMPLETE'}
          </div>
          <div className="flex-1 h-px bg-white opacity-20"></div>
        </div>
      </div>

      {/* Corner markers */}
      <div className="absolute top-0 left-0 w-20 h-20">
        <div className="absolute top-4 left-4 w-4 h-4 border-t-2 border-l-2 border-white opacity-50"></div>
      </div>
      <div className="absolute top-0 right-0 w-20 h-20">
        <div className="absolute top-4 right-4 w-4 h-4 border-t-2 border-r-2 border-white opacity-50"></div>
      </div>
      <div className="absolute bottom-0 left-0 w-20 h-20">
        <div className="absolute bottom-4 left-4 w-4 h-4 border-b-2 border-l-2 border-white opacity-50"></div>
      </div>
      <div className="absolute bottom-0 right-0 w-20 h-20">
        <div className="absolute bottom-4 right-4 w-4 h-4 border-b-2 border-r-2 border-white opacity-50"></div>
      </div>
    </div>
  );
}