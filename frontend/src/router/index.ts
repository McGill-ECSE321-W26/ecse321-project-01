import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import LoginPage from '../pages/LoginPage.vue'
import RegisterPage from '../pages/RegisterPage.vue'
import NotFoundPage from '../pages/NotFoundPage.vue'
import AccountPage from '../pages/AccountPage.vue'
import CartPage from '../pages/CartPage.vue'
import ShopPage from '../pages/ShopPage.vue'
import OrdersPage from '../pages/OrdersPage.vue'

// List of routes that do not require auth
const publicRoutes = ['home', 'login', 'register', 'not-found']

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomePage },
    { path: '/login', name: 'login', component: LoginPage },
    { path: '/register', name: 'register', component: RegisterPage },
    { path: '/account', name: 'account', component: AccountPage },
    { path: '/cart', name: 'cart', component: CartPage },
    { path: '/shop', name: 'shop', component: ShopPage },
    { path: '/orders', name: 'orders', component: OrdersPage },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: NotFoundPage },
  ],
})

// Docs: https://router.vuejs.org/guide/advanced/navigation-guards.html
// Navigation guard/Middleware (to is the target destination route, i.e. navigate TO route)
router.beforeEach((to, _from, next) => {
  // Boolean to check if JWT token exists in local storage. !! to convert to bool
  const isAuthenticated = !!localStorage.getItem('token')
  // Boolean to check if destination route matches by name
  const isRoutePublic = publicRoutes.includes(to.name as string)

  // Redirect to login page if trying to access protected route
  if (!isAuthenticated && !isRoutePublic) {
    next({ name: 'login' })
  }
  else if (isAuthenticated && (to.name === 'login' || to.name === 'register')) {
    next({ name: 'shop' })
  }
  else {
    next()
  }
})

export default router