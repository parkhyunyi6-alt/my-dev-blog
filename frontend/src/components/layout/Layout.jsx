import { useState } from 'react'
import Header from './Header.jsx'
import Sidebar from './Sidebar.jsx'
import './layout.css'

export default function Layout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false)

  function closeSidebar() { setSidebarOpen(false) }
  function toggleSidebar() { setSidebarOpen((o) => !o) }

  return (
    <>
      <Header onMenuToggle={toggleSidebar} />
      <div className="layout-body">
        {sidebarOpen && <div className="sidebar-overlay" onClick={closeSidebar} />}
        <Sidebar open={sidebarOpen} onClose={closeSidebar} />
        <main className="layout-main">
          {children}
        </main>
      </div>
    </>
  )
}
