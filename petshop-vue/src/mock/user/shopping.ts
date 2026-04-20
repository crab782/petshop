import Mock from 'mockjs'
import { products, getProductById } from './data/products'
import { orders, getOrderById } from './data/orders'

const productFavorites: number[] = []

const productReviews = [
  {
    id: 1,
    productId: 1,
    userId: 1,
    username: '爱猫人士',
    avatar: 'https://picsum.photos/100/100?random=101',
    rating: 5,
    comment: '猫咪很喜欢吃，毛色变得更亮了，性价比很高！',
    createdAt: '2024-01-20 14:30:00'
  },
  {
    id: 2,
    productId: 1,
    userId: 2,
    username: '铲屎官小王',
    avatar: 'https://picsum.photos/100/100?random=102',
    rating: 4,
    comment: '品质不错，就是价格有点贵，希望能多搞活动。',
    createdAt: '2024-01-21 16:45:00'
  },
  {
    id: 3,
    productId: 1,
    userId: 3,
    username: '猫咪主人',
    avatar: 'https://picsum.photos/100/100?random=103',
    rating: 5,
    comment: '回购第三次了，猫咪一直吃这个牌子，很健康！',
    createdAt: '2024-01-22 10:15:00'
  },
  {
    id: 4,
    productId: 2,
    userId: 4,
    username: '狗狗爸爸',
    avatar: 'https://picsum.photos/100/100?random=104',
    rating: 5,
    comment: '狗狗吃了不拉肚子，营养很好，强烈推荐！',
    createdAt: '2024-01-23 11:20:00'
  },
  {
    id: 5,
    productId: 2,
    userId: 5,
    username: '养狗达人',
    avatar: 'https://picsum.photos/100/100?random=105',
    rating: 4,
    comment: '无谷配方很好，狗狗肠胃敏感也能吃。',
    createdAt: '2024-01-24 09:30:00'
  }
]

interface CartItem {
  id: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  stock: number
  merchantId: number
  merchantName: string
  selected: boolean
}

let cartItems: CartItem[] = [
  {
    id: 1,
    productId: 1,
    productName: '皇家猫粮 成猫通用型 2kg',
    productImage: 'https://picsum.photos/400/400?random=1',
    price: 128.00,
    quantity: 2,
    stock: 156,
    merchantId: 1,
    merchantName: '宠物乐园',
    selected: true
  },
  {
    id: 2,
    productId: 3,
    productName: '猫抓板 瓦楞纸猫咪磨爪器',
    productImage: 'https://picsum.photos/400/400?random=3',
    price: 29.90,
    quantity: 1,
    stock: 320,
    merchantId: 2,
    merchantName: '萌宠之家',
    selected: true
  },
  {
    id: 3,
    productId: 9,
    productName: '宠物钙片 营养补充剂 200片装',
    productImage: 'https://picsum.photos/400/400?random=9',
    price: 89.00,
    quantity: 1,
    stock: 178,
    merchantId: 3,
    merchantName: '爱宠世界',
    selected: false
  }
]

let nextCartId = 4
let nextOrderId = 6
let nextOrderNo = 6

const generateOrderNo = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const seq = String(nextOrderNo++).padStart(4, '0')
  return `ORD${year}${month}${day}${seq}`
}

Mock.mock(RegExp('/api/products/\\d+$'), 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/products\/(\d+)/)?.[1] || '0')
  const product = getProductById(id)
  
  if (!product) {
    return {
      code: 404,
      message: '商品不存在',
      data: null
    }
  }

  const isFavorited = productFavorites.includes(product.id)
  
  return {
    code: 200,
    message: 'success',
    data: {
      ...product,
      isFavorited
    }
  }
})

Mock.mock(RegExp('/api/products/\\d+/reviews.*'), 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/products\/(\d+)/)?.[1] || '0')
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const size = parseInt(url.searchParams.get('size') || '10')
  const rating = url.searchParams.get('rating')
  
  let filteredReviews = productReviews.filter(r => r.productId === id)
  
  if (rating) {
    filteredReviews = filteredReviews.filter(r => r.rating === parseInt(rating))
  }
  
  const total = filteredReviews.length
  const start = (page - 1) * size
  const end = start + size
  const list = filteredReviews.slice(start, end)
  
  const avgRating = filteredReviews.length > 0 
    ? (filteredReviews.reduce((sum, r) => sum + r.rating, 0) / filteredReviews.length).toFixed(1)
    : '0.0'
  
  const ratingDistribution = {
    5: filteredReviews.filter(r => r.rating === 5).length,
    4: filteredReviews.filter(r => r.rating === 4).length,
    3: filteredReviews.filter(r => r.rating === 3).length,
    2: filteredReviews.filter(r => r.rating === 2).length,
    1: filteredReviews.filter(r => r.rating === 1).length
  }
  
  return {
    code: 200,
    message: 'success',
    data: {
      total,
      page,
      size,
      list,
      avgRating,
      ratingDistribution
    }
  }
})

Mock.mock('/api/favorites/products', 'post', (req: { body: string }) => {
  const { productId } = JSON.parse(req.body)
  
  if (!productId) {
    return {
      code: 400,
      message: '商品ID不能为空',
      data: null
    }
  }
  
  const product = getProductById(productId)
  if (!product) {
    return {
      code: 404,
      message: '商品不存在',
      data: null
    }
  }
  
  if (productFavorites.includes(productId)) {
    return {
      code: 400,
      message: '已收藏该商品',
      data: null
    }
  }
  
  productFavorites.push(productId)
  
  return {
    code: 200,
    message: '收藏成功',
    data: {
      productId
    }
  }
})

Mock.mock(RegExp('/api/favorites/products/\\d+$'), 'delete', (req: { url: string }) => {
  const productId = parseInt(req.url.match(/\/api\/favorites\/products\/(\d+)/)?.[1] || '0')
  
  const index = productFavorites.indexOf(productId)
  if (index === -1) {
    return {
      code: 404,
      message: '未收藏该商品',
      data: null
    }
  }
  
  productFavorites.splice(index, 1)
  
  return {
    code: 200,
    message: '取消收藏成功',
    data: null
  }
})

Mock.mock('/api/cart', 'get', () => {
  const selectedItems = cartItems.filter(item => item.selected)
  const totalPrice = selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const totalCount = selectedItems.reduce((sum, item) => sum + item.quantity, 0)
  
  return {
    code: 200,
    message: 'success',
    data: {
      items: cartItems,
      totalPrice: totalPrice.toFixed(2),
      totalCount,
      selectedCount: selectedItems.length
    }
  }
})

Mock.mock('/api/cart', 'post', (req: { body: string }) => {
  const { productId, quantity = 1 } = JSON.parse(req.body)
  
  if (!productId) {
    return {
      code: 400,
      message: '商品ID不能为空',
      data: null
    }
  }
  
  const product = getProductById(productId)
  if (!product) {
    return {
      code: 404,
      message: '商品不存在',
      data: null
    }
  }
  
  if (product.stock === 0) {
    return {
      code: 400,
      message: '商品已售罄',
      data: null
    }
  }
  
  const existingItem = cartItems.find(item => item.productId === productId)
  
  if (existingItem) {
    const newQuantity = existingItem.quantity + quantity
    if (newQuantity > product.stock) {
      return {
        code: 400,
        message: '库存不足',
        data: null
      }
    }
    existingItem.quantity = newQuantity
    
    return {
      code: 200,
      message: '已更新购物车商品数量',
      data: existingItem
    }
  }
  
  if (quantity > product.stock) {
    return {
      code: 400,
      message: '库存不足',
      data: null
    }
  }
  
  const newCartItem: CartItem = {
    id: nextCartId++,
    productId: product.id,
    productName: product.name,
    productImage: product.image,
    price: product.price,
    quantity,
    stock: product.stock,
    merchantId: product.merchantId,
    merchantName: product.merchantName,
    selected: true
  }
  
  cartItems.push(newCartItem)
  
  return {
    code: 200,
    message: '添加成功',
    data: newCartItem
  }
})

Mock.mock(RegExp('/api/cart/\\d+$'), 'put', (req: { url: string; body: string }) => {
  const cartId = parseInt(req.url.match(/\/api\/cart\/(\d+)/)?.[1] || '0')
  const { quantity, selected } = JSON.parse(req.body)
  
  const itemIndex = cartItems.findIndex(item => item.id === cartId)
  
  if (itemIndex === -1) {
    return {
      code: 404,
      message: '购物车商品不存在',
      data: null
    }
  }
  
  const item = cartItems[itemIndex]
  
  if (quantity !== undefined) {
    if (quantity <= 0) {
      return {
        code: 400,
        message: '数量必须大于0',
        data: null
      }
    }
    
    if (quantity > item.stock) {
      return {
        code: 400,
        message: '库存不足',
        data: null
      }
    }
    
    item.quantity = quantity
  }
  
  if (selected !== undefined) {
    item.selected = selected
  }
  
  return {
    code: 200,
    message: '更新成功',
    data: item
  }
})

Mock.mock(RegExp('/api/cart/\\d+$'), 'delete', (req: { url: string }) => {
  const cartId = parseInt(req.url.match(/\/api\/cart\/(\d+)/)?.[1] || '0')
  
  const itemIndex = cartItems.findIndex(item => item.id === cartId)
  
  if (itemIndex === -1) {
    return {
      code: 404,
      message: '购物车商品不存在',
      data: null
    }
  }
  
  cartItems.splice(itemIndex, 1)
  
  return {
    code: 200,
    message: '删除成功',
    data: null
  }
})

Mock.mock('/api/orders', 'post', (req: { body: string }) => {
  const { items, shippingAddress, remark } = JSON.parse(req.body)
  
  if (!items || !Array.isArray(items) || items.length === 0) {
    return {
      code: 400,
      message: '订单商品不能为空',
      data: null
    }
  }
  
  if (!shippingAddress) {
    return {
      code: 400,
      message: '收货地址不能为空',
      data: null
    }
  }
  
  const orderItems = items.map((item: { productId: number; quantity: number }) => {
    const product = getProductById(item.productId)
    return {
      productId: item.productId,
      productName: product?.name || '未知商品',
      productImage: product?.image || '',
      quantity: item.quantity,
      price: product?.price || 0,
      subtotal: (product?.price || 0) * item.quantity
    }
  })
  
  const totalPrice = orderItems.reduce((sum: number, item: { subtotal: number }) => sum + item.subtotal, 0)
  
  const merchantId = items[0]?.merchantId || 1
  const merchantName = items[0]?.merchantName || '宠物乐园'
  
  const newOrder = {
    id: nextOrderId++,
    orderNo: generateOrderNo(),
    userId: 1,
    merchantId,
    merchantName,
    status: 'pending',
    totalPrice,
    items: orderItems,
    shippingAddress,
    remark: remark || '',
    createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }
  
  orders.push(newOrder)
  
  return {
    code: 200,
    message: '订单创建成功',
    data: {
      orderId: newOrder.id,
      orderNo: newOrder.orderNo,
      totalPrice: newOrder.totalPrice
    }
  }
})

Mock.mock(RegExp('/api/orders\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const size = parseInt(url.searchParams.get('size') || '10')
  const status = url.searchParams.get('status')
  
  let filteredOrders = [...orders]
  
  if (status && status !== 'all') {
    filteredOrders = filteredOrders.filter(o => o.status === status)
  }
  
  filteredOrders.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  
  const total = filteredOrders.length
  const start = (page - 1) * size
  const end = start + size
  const list = filteredOrders.slice(start, end)
  
  const statusCount = {
    all: orders.length,
    pending: orders.filter(o => o.status === 'pending').length,
    paid: orders.filter(o => o.status === 'paid').length,
    shipped: orders.filter(o => o.status === 'shipped').length,
    completed: orders.filter(o => o.status === 'completed').length,
    cancelled: orders.filter(o => o.status === 'cancelled').length
  }
  
  return {
    code: 200,
    message: 'success',
    data: {
      total,
      page,
      size,
      list,
      statusCount
    }
  }
})

Mock.mock(RegExp('/api/orders/\\d+$'), 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/orders\/(\d+)/)?.[1] || '0')
  const order = getOrderById(id)
  
  if (!order) {
    return {
      code: 404,
      message: '订单不存在',
      data: null
    }
  }
  
  return {
    code: 200,
    message: 'success',
    data: order
  }
})

Mock.mock(RegExp('/api/orders/\\d+/cancel$'), 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/orders\/(\d+)/)?.[1] || '0')
  const order = getOrderById(id)
  
  if (!order) {
    return {
      code: 404,
      message: '订单不存在',
      data: null
    }
  }
  
  if (order.status === 'completed') {
    return {
      code: 400,
      message: '已完成的订单不能取消',
      data: null
    }
  }
  
  if (order.status === 'cancelled') {
    return {
      code: 400,
      message: '订单已取消',
      data: null
    }
  }
  
  if (order.status === 'shipped') {
    return {
      code: 400,
      message: '已发货的订单不能取消，请联系客服处理',
      data: null
    }
  }
  
  order.status = 'cancelled'
  order.cancelledAt = new Date().toISOString().replace('T', ' ').substring(0, 19)
  
  return {
    code: 200,
    message: '订单已取消',
    data: {
      id: order.id,
      status: order.status
    }
  }
})

Mock.mock(RegExp('/api/orders/\\d+/confirm$'), 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/orders\/(\d+)/)?.[1] || '0')
  const order = getOrderById(id)
  
  if (!order) {
    return {
      code: 404,
      message: '订单不存在',
      data: null
    }
  }
  
  if (order.status !== 'shipped') {
    return {
      code: 400,
      message: '只有已发货的订单才能确认收货',
      data: null
    }
  }
  
  order.status = 'completed'
  order.completedAt = new Date().toISOString().replace('T', ' ').substring(0, 19)
  
  return {
    code: 200,
    message: '确认收货成功',
    data: {
      id: order.id,
      status: order.status
    }
  }
})

Mock.mock(RegExp('/api/orders/\\d+$'), 'delete', (req: { url: string }) => {
  const orderId = parseInt(req.url.match(/\/api\/orders\/(\d+)/)?.[1] || '0')
  const orderIndex = orders.findIndex(o => o.id === orderId)
  
  if (orderIndex === -1) {
    return {
      code: 404,
      message: '订单不存在',
      data: null
    }
  }
  
  const order = orders[orderIndex]
  
  if (order.status !== 'completed' && order.status !== 'cancelled') {
    return {
      code: 400,
      message: '只能删除已完成或已取消的订单',
      data: null
    }
  }
  
  orders.splice(orderIndex, 1)
  
  return {
    code: 200,
    message: '订单已删除',
    data: null
  }
})

Mock.mock('/api/orders/batch-cancel', 'post', (req: { body: string }) => {
  const { ids } = JSON.parse(req.body)
  
  if (!ids || !Array.isArray(ids) || ids.length === 0) {
    return {
      code: 400,
      message: '请选择要取消的订单',
      data: null
    }
  }
  
  const cancelledOrders: number[] = []
  const failedOrders: { id: number; reason: string }[] = []
  
  ids.forEach((id: number) => {
    const order = getOrderById(id)
    if (!order) {
      failedOrders.push({ id, reason: '订单不存在' })
      return
    }
    
    if (order.status === 'completed' || order.status === 'cancelled' || order.status === 'shipped') {
      failedOrders.push({ id, reason: '订单状态不允许取消' })
      return
    }
    
    order.status = 'cancelled'
    order.cancelledAt = new Date().toISOString().replace('T', ' ').substring(0, 19)
    cancelledOrders.push(id)
  })
  
  return {
    code: 200,
    message: `成功取消${cancelledOrders.length}个订单`,
    data: {
      cancelledCount: cancelledOrders.length,
      failedCount: failedOrders.length,
      failedOrders
    }
  }
})

Mock.mock('/api/orders/batch-delete', 'post', (req: { body: string }) => {
  const { ids } = JSON.parse(req.body)
  
  if (!ids || !Array.isArray(ids) || ids.length === 0) {
    return {
      code: 400,
      message: '请选择要删除的订单',
      data: null
    }
  }
  
  const deletedOrders: number[] = []
  const failedOrders: { id: number; reason: string }[] = []
  
  ids.forEach((id: number) => {
    const orderIndex = orders.findIndex(o => o.id === id)
    if (orderIndex === -1) {
      failedOrders.push({ id, reason: '订单不存在' })
      return
    }
    
    const order = orders[orderIndex]
    if (order.status !== 'completed' && order.status !== 'cancelled') {
      failedOrders.push({ id, reason: '只能删除已完成或已取消的订单' })
      return
    }
    
    orders.splice(orderIndex, 1)
    deletedOrders.push(id)
  })
  
  return {
    code: 200,
    message: `成功删除${deletedOrders.length}个订单`,
    data: {
      deletedCount: deletedOrders.length,
      failedCount: failedOrders.length,
      failedOrders
    }
  }
})

export function setupUserShoppingMock() {
  console.log('用户购物Mock服务已配置')
}
