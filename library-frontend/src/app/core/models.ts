export interface Book { id?: number; title: string; author: string; category: string; publicationYear: number | null; available: boolean; }
export interface Member { id?: number; firstName: string; lastName: string; email: string; phone: string; }
export interface Borrowing { id: number; bookId: number; memberId: number; borrowingDate: string; dueDate: string; returnDate: string | null; status: 'BORROWED' | 'RETURNED'; }
export interface Reservation { id: number; bookId: number; memberId: number; reservationDate: string; status: 'PENDING' | 'CANCELLED' | string; }
export interface Fine { id: number; memberId: number; borrowingId: number; amount: number; reason: string; createdDate: string; status: 'UNPAID' | 'PAID'; }
export interface Review { id: number; bookId: number; memberId: number; rating: number; comment: string; }
export interface AppNotification { id: number; memberId: number; message: string; createdAt: string; }
export interface BookDetails { book: Book; reviews: Review[]; averageRating: number; reviewCount: number; }
export interface GlobalStats { totalBooks: number; availableBooks: number; totalBorrowings: number; ongoingBorrowings: number; source?: string; }
export interface BookStats { totalBooks: number; availableBooks: number; borrowedBooks: number; booksByCategory: Record<string, number>; }
