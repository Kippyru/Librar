import React from 'react';
import { RouterProvider } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import { router } from './routes/AppRoutes';
import esES from 'antd/locale/es_ES'; // Idioma en español para componentes de AntD

const App: React.FC = () => {
  return (
    <ConfigProvider locale={esES} theme={{ token: { colorPrimary: '#1677ff' } }}>
      <RouterProvider router={router} />
    </ConfigProvider>
  );
};

export default App;