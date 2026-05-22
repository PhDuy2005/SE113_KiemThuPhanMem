import { Product, ProductStatus } from '../models/ui_types/product';
import { Order, OrderStatus } from '../models/ui_types/order';
import { User, UserStatus } from '../models/ui_types/user';
import { Category } from '../models/ui_types/category';
import { CartItem } from '../models/ui_types/cart';
import { PaymentMethod } from '../models/ui_types/paymentMethod';
import { Address } from '../models/ui_types/address';
import { Review, ReviewStatus } from '../models/ui_types/review';
import { Voucher, VoucherType } from '../models/ui_types/voucher';
import { Notification } from '../models/ui_types/notification';

export let mockCart: CartItem[] = [];

export const categories: Category[] = [
  { id: 'cat1', name: 'Laptops', productCount: 124, createdAt: '2024-01-01T00:00:00Z' },
  { id: 'cat2', name: 'Smartphones', productCount: 86, createdAt: '2024-01-01T00:00:00Z' },
  { id: 'cat3', name: 'Monitors', productCount: 52, createdAt: '2024-01-01T00:00:00Z' },
  { id: 'cat4', name: 'Gaming', productCount: 43, createdAt: '2024-01-01T00:00:00Z' },
  { id: 'cat5', name: 'Accessories', productCount: 95, createdAt: '2024-01-01T00:00:00Z' },
  { id: 'cat6', name: 'Audio', productCount: 28, createdAt: '2024-01-01T00:00:00Z' },
];

export const paymentMethods: PaymentMethod[] = [
  { 
    id: 'pm1', 
    name: 'Credit Card', 
    isActive: true, 
    createdAt: '2024-01-01T00:00:00Z',
    description: 'Pay with Visa, Mastercard, or AMEX'
  },
  { 
    id: 'pm2', 
    name: 'E-Wallet', 
    isActive: true, 
    createdAt: '2024-01-01T00:00:00Z',
    description: 'Pay with PayPal, Apple Pay, or Google Pay'
  },
  { 
    id: 'pm3', 
    name: 'Bank Transfer', 
    isActive: true, 
    createdAt: '2024-01-01T00:00:00Z',
    description: 'Direct transfer from your bank account'
  }
];

export const products: Product[] = [
  {
    id: 'p1',
    name: 'MacBook Pro 16"',
    description: 'Powerful laptop with M3 Max chip, 36GB RAM, 1TB SSD',
    price: 2499,
    status: ProductStatus.ACTIVE,
    stock: 45,
    imageUrl: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca4?w=400',
    categoryName: 'Laptops',
    rating: 4.8,
    reviewsCount: 234,
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'p2',
    name: 'Dell XPS 15',
    description: 'Premium laptop with Intel i9, 32GB RAM, 1TB SSD',
    price: 1899,
    status: ProductStatus.ACTIVE,
    stock: 32,
    imageUrl: 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400',
    categoryName: 'Laptops',
    brand: 'Dell',
    rating: 4.6,
    reviewsCount: 187,
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'p3',
    name: 'iPhone 15 Pro',
    description: 'Latest iPhone with A17 Pro chip, 256GB storage',
    price: 999,
    status: ProductStatus.ACTIVE,
    stock: 128,
    imageUrl: 'https://images.unsplash.com/photo-1592286927505-c0d1981d9f09?w=400',
    categoryName: 'Smartphones',
    brand: 'Apple',
    rating: 4.9,
    reviewsCount: 512,
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'p4',
    name: 'Samsung Galaxy S24 Ultra',
    description: 'Premium Android phone with S Pen, 512GB storage',
    price: 1199,
    status: ProductStatus.ACTIVE,
    stock: 95,
    imageUrl: 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400',
    categoryName: 'Smartphones',
    brand: 'Samsung',
    rating: 4.7,
    reviewsCount: 389,
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'p5',
    name: 'LG UltraWide 34"',
    description: '34-inch curved monitor, 3440x1440, 144Hz',
    price: 599,
    status: ProductStatus.ACTIVE,
    stock: 67,
    imageUrl: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400',
    categoryName: 'Monitors',
    brand: 'LG',
    rating: 4.5,
    reviewsCount: 156,
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'p6',
    name: 'Logitech MX Master 3S',
    description: 'Premium wireless mouse with customizable buttons',
    price: 99,
    status: ProductStatus.ACTIVE,
    stock: 234,
    imageUrl: 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400',
    categoryName: 'Accessories',
    brand: 'Logitech',
    rating: 4.8,
    reviewsCount: 892,
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'p7',
    name: 'Keychron K8 Pro',
    description: 'Mechanical keyboard, hot-swappable switches, RGB',
    price: 109,
    status: ProductStatus.ACTIVE,
    stock: 156,
    imageUrl: 'https://images.unsplash.com/photo-1595225476474-87563907a212?w=400',
    categoryName: 'Accessories',
    brand: 'Keychron',
    rating: 4.7,
    reviewsCount: 445,
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'p8',
    name: 'Sony WH-1000XM5',
    description: 'Premium noise-cancelling headphones, 30hr battery',
    price: 399,
    status: ProductStatus.ACTIVE,
    stock: 89,
    imageUrl: 'https://images.unsplash.com/photo-1545127398-14699f92334b?w=400',
    categoryName: 'Audio',
    brand: 'Sony',
    rating: 4.9,
    reviewsCount: 678,
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'p9',
    name: 'PlayStation 5',
    description: 'Next-gen gaming console with 825GB SSD',
    price: 499,
    status: ProductStatus.ACTIVE,
    stock: 43,
    imageUrl: 'https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=400',
    categoryName: 'Gaming',
    brand: 'Sony',
    rating: 4.8,
    reviewsCount: 1234,
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'p10',
    name: 'AirPods Pro 2',
    description: 'True wireless earbuds with active noise cancellation',
    price: 249,
    status: ProductStatus.ACTIVE,
    stock: 178,
    imageUrl: 'https://images.unsplash.com/photo-1606841837239-c5a1a4a07af7?w=400',
    categoryName: 'Accessories',
    brand: 'Apple',
    rating: 4.7,
    reviewsCount: 567,
    createdAt: '2024-01-01T00:00:00Z',
  },
];

export let orders: Order[] = [
  {
    id: 'ORD-001',
    userId: 'u4',
    status: OrderStatus.PENDING,
    totalProductAmount: 2499,
    shippingFee: 0,
    discountAmount: 0,
    totalAmount: 2499,
    customerName: 'Demo Customer',
    shippingAddressSnapshot: '123 Tech Avenue, Ben Nghe Ward, Ho Chi Minh City',
    paymentMethodName: 'Credit Card',
    createdAt: new Date().toISOString(),
    items: [
      { orderId: 'ORD-001', productId: 'p1', productName: 'MacBook Pro 16"', quantity: 1, price: 2499, imageUrl: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca4?w=400' },
    ],
  },
  {
    id: 'ORD-002',
    userId: 'u4',
    status: OrderStatus.APPROVED,
    totalProductAmount: 999,
    shippingFee: 0,
    discountAmount: 50,
    totalAmount: 949,
    customerName: 'Demo Customer',
    shippingAddressSnapshot: '123 Tech Avenue, Ben Nghe Ward, Ho Chi Minh City',
    paymentMethodName: 'E-Wallet',
    createdAt: new Date(Date.now() - 86400000).toISOString(), // 1 day ago
    items: [
      { orderId: 'ORD-002', productId: 'p3', productName: 'iPhone 15 Pro', quantity: 1, price: 999, imageUrl: 'https://images.unsplash.com/photo-1592286927505-c0d1981d9f09?w=400' },
    ],
  },
  {
    id: 'ORD-003',
    userId: 'u4',
    status: OrderStatus.SHIPPING,
    totalProductAmount: 199,
    shippingFee: 20,
    discountAmount: 0,
    totalAmount: 219,
    customerName: 'Demo Customer',
    shippingAddressSnapshot: '123 Tech Avenue, Ben Nghe Ward, Ho Chi Minh City',
    paymentMethodName: 'Bank Transfer',
    createdAt: new Date(Date.now() - 172800000).toISOString(), // 2 days ago
    items: [
      { orderId: 'ORD-003', productId: 'p6', productName: 'Logitech MX Master 3S', quantity: 2, price: 99.5, imageUrl: 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400' },
    ],
  },
  {
    id: 'ORD-004',
    userId: 'u4',
    status: OrderStatus.DELIVERED,
    totalProductAmount: 399,
    shippingFee: 0,
    discountAmount: 0,
    totalAmount: 399,
    customerName: 'Demo Customer',
    shippingAddressSnapshot: '123 Tech Avenue, Ben Nghe Ward, Ho Chi Minh City',
    paymentMethodName: 'Credit Card',
    createdAt: new Date(Date.now() - 259200000).toISOString(), // 3 days ago
    items: [
      { orderId: 'ORD-004', productId: 'p8', productName: 'Sony WH-1000XM5', quantity: 1, price: 399, imageUrl: 'https://images.unsplash.com/photo-1545127398-14699f92334b?w=400' },
    ],
  },
];

export const users: User[] = [
  {
    id: 'u1',
    email: 'admin@techsales.com',
    status: UserStatus.ACTIVE,
    role: 'TechnicalAdmin',
    fullName: 'Technical Admin',
    phone: '+1 (555) 000-1111',
    password: 'password',
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'u2',
    email: 'business@techsales.com',
    status: UserStatus.ACTIVE,
    role: 'BusinessAdmin',
    fullName: 'Business Manager',
    phone: '+1 (555) 000-2222',
    password: 'password',
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'u3',
    email: 'sales@techsales.com',
    status: UserStatus.ACTIVE,
    role: 'Staff',
    fullName: 'Sales Representative',
    phone: '+1 (555) 000-3333',
    password: 'password',
    createdAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'u4',
    email: 'customer@email.com',
    status: UserStatus.ACTIVE,
    role: 'Customer',
    fullName: 'Demo Customer',
    phone: '+1 (555) 999-8888',
    password: 'password',
    createdAt: '2025-01-01T00:00:00Z',
  },
];


export const salesData = [
  { month: 'Jan', revenue: 45000, orders: 234 },
  { month: 'Feb', revenue: 52000, orders: 289 },
  { month: 'Mar', revenue: 48000, orders: 256 },
  { month: 'Apr', revenue: 61000, orders: 334 },
  { month: 'May', revenue: 55000, orders: 298 },
  { month: 'Jun', revenue: 67000, orders: 367 },
];

export const categoryData = [
  { name: 'Laptops', value: 35 },
  { name: 'Smartphones', value: 28 },
  { name: 'Monitors', value: 15 },
  { name: 'Accessories', value: 12 },
  { name: 'Gaming', value: 10 },
];

export let mockAddresses: Address[] = [
  {
    id: '1',
    province: 'Ho Chi Minh City',
    ward: 'Ben Nghe Ward',
    detail: '123 Tech Avenue',
    isDefault: true,
  },
  {
    id: '2',
    province: 'Ho Chi Minh City',
    ward: 'Ward 12',
    detail: '456 Innovation Park',
    isDefault: false,
  }
];

export let mockReviews: Review[] = [
  {
    id: 'rev-1',
    userId: 'u4',
    userName: 'Demo Customer',
    productId: 'p1',
    rating: 5,
    comment: 'Amazing performance! The M3 chip is a beast.',
    status: ReviewStatus.VISIBLE,
    createdAt: new Date(Date.now() - 86400000 * 2).toISOString(),
    responses: [
      {
        id: 'resp-1',
        reviewId: 'rev-1',
        userId: 'u3',
        userName: 'Tech Support',
        content: 'Thank you for your feedback! We are glad you enjoy the M3 performance.',
        createdAt: new Date(Date.now() - 86400000).toISOString()
      },
      {
        id: 'resp-2',
        reviewId: 'rev-1',
        userId: 'u2',
        userName: 'Business Manager',
        content: 'We prioritize top-tier performance for our professional users.',
        createdAt: new Date(Date.now() - 43200000).toISOString()
      }
    ]
  },
  {
    id: 'rev-2',
    userId: 'u4',
    userName: 'Demo Customer',
    productId: 'p1',
    rating: 4,
    comment: 'Great screen, but quite expensive.',
    status: ReviewStatus.VISIBLE,
    createdAt: new Date(Date.now() - 86400000 * 5).toISOString()
  }
];

export const vouchers: Voucher[] = [
  {
    id: 'v1',
    code: 'TECHWELCOME',
    name: 'Welcome Pack',
    description: 'Special discount for first-time premium technology adopters.',
    type: VoucherType.FIXED,
    value: 50,
    minOrderAmount: 200,
    startDate: '2026-01-01',
    endDate: '2026-12-31',
    usageCount: 0,
    isActive: true
  },
  {
    id: 'v2',
    code: 'SUMMER26',
    name: 'Summer Sale',
    description: 'Beat the heat with 15% off on all ecosystem hardware.',
    type: VoucherType.PERCENTAGE,
    value: 15,
    maxDiscountAmount: 100,
    minOrderAmount: 100,
    startDate: '2026-05-01',
    endDate: '2026-08-31',
    usageCount: 45,
    isActive: true
  },
  {
    id: 'v3',
    code: 'INDUSTRIAL',
    name: 'Industry Expert',
    description: 'Flat $200 off for orders exceeding $2,000.',
    type: VoucherType.FIXED,
    value: 200,
    minOrderAmount: 2000,
    startDate: '2026-01-01',
    endDate: '2026-12-31',
    usageCount: 12,
    isActive: true
  }
];

export const mockNotifications: Notification[] = [
  {
    id: 'n1',
    userId: 'u4',
    title: 'Order Protocol Initialized',
    message: 'Your order #ORD-001 has been successfully placed in the shipping queue.',
    type: 'order',
    isRead: false,
    createdAt: new Date(Date.now() - 3600000).toISOString(),
    link: '/customer/orders/ORD-001'
  },
  {
    id: 'n2',
    userId: 'u4',
    title: 'Security Alert',
    message: 'New login detected from a secure terminal in Hanoi, VN.',
    type: 'security',
    isRead: true,
    createdAt: new Date(Date.now() - 86400000).toISOString(),
  },
  {
    id: 'n3',
    userId: 'u4',
    title: 'System Synced',
    message: 'Hardware firmware update v2.4.0 is now available for your ecosystem.',
    type: 'system',
    isRead: false,
    createdAt: new Date(Date.now() - 172800000).toISOString(),
  },
  {
    id: 'n4',
    userId: 'u4',
    title: 'Exclusive Access',
    message: 'New industrial monitors have been added to the catalog. Check them out now!',
    type: 'promo',
    isRead: true,
    createdAt: new Date(Date.now() - 259200000).toISOString(),
    link: '/customer/products'
  }
];
