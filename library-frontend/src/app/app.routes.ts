import { Routes } from '@angular/router';
import { authGuard } from './core/guards';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login').then(m => m.Login) },
  {
    path: '', canActivate: [authGuard],
    loadComponent: () => import('./shared/shell').then(m => m.Shell),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', title: 'Tableau de bord', loadComponent: () => import('./pages/dashboard').then(m => m.Dashboard) },
      { path: 'books', title: 'Livres', loadComponent: () => import('./pages/books').then(m => m.Books) },
      { path: 'books/:id', title: 'Détail du livre', loadComponent: () => import('./pages/book-detail').then(m => m.BookDetail) },
      { path: 'members', title: 'Membres', loadComponent: () => import('./pages/members').then(m => m.Members) },
      { path: 'borrowings', title: 'Emprunts', loadComponent: () => import('./pages/borrowings').then(m => m.Borrowings) },
      { path: 'reservations', title: 'Réservations', loadComponent: () => import('./pages/reservations').then(m => m.Reservations) },
      { path: 'fines', title: 'Amendes', loadComponent: () => import('./pages/fines').then(m => m.Fines) },
      { path: 'notifications', title: 'Notifications', loadComponent: () => import('./pages/notifications').then(m => m.Notifications) },
    ]
  },
  { path: '**', redirectTo: '' }
];
