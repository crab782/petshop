import { MockMethod } from 'vite-plugin-mock'
import { orders, getOrderById } from '../data/orders'
import { getProductById } from '../data/products'

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

const orderHandlers: MockMethod[] = [
  {
    url: '/api/orders',
    method: 'post',
    response: (req) => {
      const { items, shippingAddress, remark } = req.body
      
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
      
      const orderItems = items.map(item => {
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
      
      const totalPrice = orderItems.reduce((sum, item) => sum + item.subtotal, 0)
      
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
    }
  },
  {
    url: '/api/orders',
    method: 'get',
    response: (req) => {
      const { page = 1, size = 10, status } = req.query
      
      let filteredOrders = [...orders]
      
      if (status && status !== 'all') {
        filteredOrders = filteredOrders.filter(o => o.status === status)
      }
      
      filteredOrders.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      
      const total = filteredOrders.length
      const start = (parseInt(page) - 1) * parseInt(size)
      const end = start + parseInt(size)
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
          page: parseInt(page),
          size: parseInt(size),
          list,
          statusCount
        }
      }
    }
  },
  {
    url: '/api/orders/:id',
    method: 'get',
    response: (req) => {
      const { id } = req.params
      const order = getOrderById(parseInt(id))
      
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
    }
  },
  {
    url: '/api/orders/:id/cancel',
    method: 'put',
    response: (req) => {
      const { id } = req.params
      const order = getOrderById(parseInt(id))
      
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
    }
  },
  {
    url: '/api/orders/:id/confirm',
    method: 'put',
    response: (req) => {
      const { id } = req.params
      const order = getOrderById(parseInt(id))
      
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
    }
  },
  {
    url: '/api/orders/:id',
    method: 'delete',
    response: (req) => {
      const { id } = req.params
      const orderId = parseInt(id)
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
    }
  },
  {
    url: '/api/orders/batch-cancel',
    method: 'post',
    response: (req) => {
      const { ids } = req.body
      
      if (!ids || !Array.isArray(ids) || ids.length === 0) {
        return {
          code: 400,
          message: '请选择要取消的订单',
          data: null
        }
      }
      
      const cancelledOrders: number[] = []
      const failedOrders: { id: number; reason: string }[] = []
      
      ids.forEach(id => {
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
    }
  },
  {
    url: '/api/orders/batch-delete',
    method: 'post',
    response: (req) => {
      const { ids } = req.body
      
      if (!ids || !Array.isArray(ids) || ids.length === 0) {
        return {
          code: 400,
          message: '请选择要删除的订单',
          data: null
        }
      }
      
      const deletedOrders: number[] = []
      const failedOrders: { id: number; reason: string }[] = []
      
      ids.forEach(id => {
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
    }
  },
  {
    url: '/api/user/orders/preview',
    method: 'post',
    response: (req) => {
      const { items, addressId } = req.body
      
      if (!items || !Array.isArray(items) || items.length === 0) {
        return {
          code: 400,
          message: '请选择要购买的商品',
          data: null
        }
      }

      const orderItems = items.map((item: any) => {
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

      const totalPrice = orderItems.reduce((sum: number, item: any) => sum + item.subtotal, 0)
      const freight = totalPrice >= 99 ? 0 : 10

      return {
        code: 200,
        message: 'success',
        data: {
          items: orderItems,
          totalPrice,
          freight,
          totalAmount: totalPrice + freight,
          address: {
            id: 1,
            receiverName: '张三',
            phone: '13800138001',
            province: '北京市',
            city: '北京市',
            district: '朝阳区',
            detailAddress: '建国路88号SOHO现代城A座1001室',
            isDefault: true
          }
        }
      }
    }
  },
  {
    url: /\/api\/user\/orders\/(\d+)\/pay\/status$/,
    method: 'get',
    response: (req) => {
      const { id } = req.params
      const order = getOrderById(parseInt(id))
      
      if (!order) {
        return {
          code: 404,
          message: '订单不存在',
          data: null
        }
      }

      const payStatus = order.status === 'pending' ? 'unpaid' : 'paid'
      
      return {
        code: 200,
        message: 'success',
        data: {
          orderId: order.id,
          orderNo: order.orderNo,
          status: order.status,
          payStatus,
          totalPrice: order.totalPrice,
          payTime: order.paidAt || null
        }
      }
    }
  }
]

export default orderHandlers
