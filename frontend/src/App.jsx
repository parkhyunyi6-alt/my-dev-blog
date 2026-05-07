import { Routes, Route } from 'react-router-dom'
import Layout from './components/layout/Layout.jsx'
import HomePage from './pages/HomePage.jsx'
import PostDetailPage from './pages/PostDetailPage.jsx'
import TagPage from './pages/TagPage.jsx'
import PostEditorPage from './pages/PostEditorPage.jsx'
import PrivateRoute from './components/common/PrivateRoute.jsx'

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/categories/:categoryId" element={<HomePage />} />
        <Route path="/posts/:id" element={<PostDetailPage />} />
        <Route path="/tags/:tagId" element={<TagPage />} />
        <Route path="/write" element={<PrivateRoute><PostEditorPage /></PrivateRoute>} />
        <Route path="/posts/:id/edit" element={<PrivateRoute><PostEditorPage /></PrivateRoute>} />
      </Routes>
    </Layout>
  )
}

export default App
