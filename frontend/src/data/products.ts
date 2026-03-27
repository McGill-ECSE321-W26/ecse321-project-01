export interface Product {
  id: number
  name: string
  brand: string
  price: number
  colors: string[]
  tag: 'new' | 'low' | null
  img: string
  sizes: string[]
  category: 'tops' | 'bottoms' | 'outerwear' | 'accessories'
}

export const products: Product[] = [
  {
    id: 1,
    name: 'Washed Canvas Field Jacket',
    brand: 'Kloth Originals',
    price: 185,
    colors: ['#5c5a4e', '#3b3a32', '#8b7d6b'],
    tag: 'new',
    img: 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&h=800&fit=crop&crop=top',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'outerwear',
  },
  {
    id: 2,
    name: 'Relaxed Cargo Trousers',
    brand: 'Kloth Essentials',
    price: 98,
    colors: ['#c2b49a', '#3b3a32', '#6b6455'],
    tag: null,
    img: 'https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=600&h=800&fit=crop',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'bottoms',
  },
  {
    id: 3,
    name: 'Heavyweight Hoodie',
    brand: 'Kloth Originals',
    price: 120,
    colors: ['#2c3040', '#c2b49a', '#f5f0e8'],
    tag: null,
    img: 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=600&h=800&fit=crop',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'tops',
  },
  {
    id: 4,
    name: 'Oversized Graphic Tee',
    brand: 'Kloth Studio',
    price: 55,
    colors: ['#f5f0e8', '#1a1a18'],
    tag: 'new',
    img: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&h=800&fit=crop',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'tops',
  },
  {
    id: 5,
    name: 'Pleated Wide-Leg Pants',
    brand: 'Kloth Essentials',
    price: 115,
    colors: ['#c2b49a', '#1a1a18', '#6b6455'],
    tag: null,
    img: 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600&h=800&fit=crop',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'bottoms',
  },
  {
    id: 6,
    name: 'Cotton Twill Cap',
    brand: 'Kloth Accessories',
    price: 38,
    colors: ['#2c3040', '#5c5a4e', '#c2b49a'],
    tag: null,
    img: 'https://images.unsplash.com/photo-1588850561407-ed78c334e67a?w=600&h=800&fit=crop',
    sizes: ['S', 'M', 'L'],
    category: 'accessories',
  },
  {
    id: 7,
    name: 'Linen Blend Camp Shirt',
    brand: 'Kloth Studio',
    price: 78,
    colors: ['#f5f0e8', '#8b7d6b'],
    tag: 'new',
    img: 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600&h=800&fit=crop',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'tops',
  },
  {
    id: 8,
    name: 'Vintage Wash Denim Shorts',
    brand: 'Kloth Originals',
    price: 85,
    colors: ['#6888a5', '#3b3a32'],
    tag: null,
    img: 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=600&h=800&fit=crop',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'bottoms',
  },
  {
    id: 9,
    name: 'Knit Polo Sweater',
    brand: 'Kloth Studio',
    price: 95,
    colors: ['#5c5a4e', '#c2b49a', '#2c3040'],
    tag: null,
    img: 'https://images.unsplash.com/photo-1614975059251-992f11792571?w=600&h=800&fit=crop',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'tops',
  },
  {
    id: 10,
    name: 'Nylon Crossbody Bag',
    brand: 'Kloth Accessories',
    price: 65,
    colors: ['#1a1a18', '#5c5a4e'],
    tag: 'low',
    img: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600&h=800&fit=crop',
    sizes: [],
    category: 'accessories',
  },
  {
    id: 11,
    name: 'Layered Coach Jacket',
    brand: 'Kloth Originals',
    price: 165,
    colors: ['#2c3040', '#3b3a32'],
    tag: null,
    img: 'https://images.unsplash.com/photo-1544022613-e87ca75a784a?w=600&h=800&fit=crop',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'outerwear',
  },
  {
    id: 12,
    name: 'Boxy Pocket Tee',
    brand: 'Kloth Essentials',
    price: 45,
    colors: ['#f5f0e8', '#1a1a18', '#c2b49a', '#5c5a4e'],
    tag: null,
    img: 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600&h=800&fit=crop',
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    category: 'tops',
  },
]

export const categories = ['All', 'Tops', 'Bottoms', 'Outerwear', 'Accessories'] as const

export const priceRanges = ['Under $75', '$75 – $150', '$150+'] as const

export const sortOptions = [
  { label: 'Newest', value: 'newest' },
  { label: 'Price: Low → High', value: 'price-asc' },
  { label: 'Price: High → Low', value: 'price-desc' },
  { label: 'Best Selling', value: 'best-selling' },
] as const
