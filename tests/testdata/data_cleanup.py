"""
测试数据清理工具模块

提供各类测试数据的清理功能，支持级联删除。
"""

from typing import Any, List, Optional


def cleanup_users(user_ids: List[int], db_connection: Any) -> int:
    """
    清理用户数据

    级联删除关联数据：
    - 宠物数据
    - 预约数据
    - 订单数据
    - 评价数据
    - 地址数据
    - 收藏数据

    Args:
        user_ids: 用户ID列表
        db_connection: 数据库连接对象

    Returns:
        int: 删除的记录数

    使用示例：
        cleanup_users([1, 2, 3], db_connection)
    """
    if not user_ids:
        return 0

    total_deleted = 0

    try:
        cursor = db_connection.cursor()

        placeholders = ",".join(["%s"] * len(user_ids))

        cursor.execute(f"SELECT id FROM pet WHERE user_id IN ({placeholders})", user_ids)
        pet_ids = [row[0] for row in cursor.fetchall()]
        if pet_ids:
            total_deleted += cleanup_pets(pet_ids, db_connection)

        cursor.execute(f"SELECT id FROM appointment WHERE user_id IN ({placeholders})", user_ids)
        appointment_ids = [row[0] for row in cursor.fetchall()]
        if appointment_ids:
            total_deleted += cleanup_appointments(appointment_ids, db_connection)

        cursor.execute(f"SELECT id FROM product_order WHERE user_id IN ({placeholders})", user_ids)
        order_ids = [row[0] for row in cursor.fetchall()]
        if order_ids:
            total_deleted += cleanup_orders(order_ids, db_connection)

        cursor.execute(f"SELECT id FROM review WHERE user_id IN ({placeholders})", user_ids)
        review_ids = [row[0] for row in cursor.fetchall()]
        if review_ids:
            total_deleted += cleanup_reviews(review_ids, db_connection)

        cursor.execute(f"DELETE FROM address WHERE user_id IN ({placeholders})", user_ids)
        total_deleted += cursor.rowcount

        cursor.execute(f"DELETE FROM favorite WHERE user_id IN ({placeholders})", user_ids)
        total_deleted += cursor.rowcount

        cursor.execute(f"DELETE FROM cart WHERE user_id IN ({placeholders})", user_ids)
        total_deleted += cursor.rowcount

        cursor.execute(f"DELETE FROM user WHERE id IN ({placeholders})", user_ids)
        total_deleted += cursor.rowcount

        db_connection.commit()
        cursor.close()

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理用户数据失败: {str(e)}")

    return total_deleted


def cleanup_merchants(merchant_ids: List[int], db_connection: Any) -> int:
    """
    清理商家数据

    级联删除关联数据：
    - 服务数据
    - 商品数据
    - 预约数据
    - 订单数据
    - 评价数据

    Args:
        merchant_ids: 商家ID列表
        db_connection: 数据库连接对象

    Returns:
        int: 删除的记录数
    """
    if not merchant_ids:
        return 0

    total_deleted = 0

    try:
        cursor = db_connection.cursor()

        placeholders = ",".join(["%s"] * len(merchant_ids))

        cursor.execute(f"SELECT id FROM service WHERE merchant_id IN ({placeholders})", merchant_ids)
        service_ids = [row[0] for row in cursor.fetchall()]
        if service_ids:
            total_deleted += cleanup_services(service_ids, db_connection)

        cursor.execute(f"SELECT id FROM product WHERE merchant_id IN ({placeholders})", merchant_ids)
        product_ids = [row[0] for row in cursor.fetchall()]
        if product_ids:
            total_deleted += cleanup_products(product_ids, db_connection)

        cursor.execute(f"SELECT id FROM appointment WHERE merchant_id IN ({placeholders})", merchant_ids)
        appointment_ids = [row[0] for row in cursor.fetchall()]
        if appointment_ids:
            total_deleted += cleanup_appointments(appointment_ids, db_connection)

        cursor.execute(f"SELECT id FROM product_order WHERE merchant_id IN ({placeholders})", merchant_ids)
        order_ids = [row[0] for row in cursor.fetchall()]
        if order_ids:
            total_deleted += cleanup_orders(order_ids, db_connection)

        cursor.execute(f"SELECT id FROM review WHERE merchant_id IN ({placeholders})", merchant_ids)
        review_ids = [row[0] for row in cursor.fetchall()]
        if review_ids:
            total_deleted += cleanup_reviews(review_ids, db_connection)

        cursor.execute(f"DELETE FROM merchant WHERE id IN ({placeholders})", merchant_ids)
        total_deleted += cursor.rowcount

        db_connection.commit()
        cursor.close()

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理商家数据失败: {str(e)}")

    return total_deleted


def cleanup_services(service_ids: List[int], db_connection: Any) -> int:
    """
    清理服务数据

    级联删除关联数据：
    - 预约数据
    - 评价数据

    Args:
        service_ids: 服务ID列表
        db_connection: 数据库连接对象

    Returns:
        int: 删除的记录数
    """
    if not service_ids:
        return 0

    total_deleted = 0

    try:
        cursor = db_connection.cursor()

        placeholders = ",".join(["%s"] * len(service_ids))

        cursor.execute(f"SELECT id FROM appointment WHERE service_id IN ({placeholders})", service_ids)
        appointment_ids = [row[0] for row in cursor.fetchall()]
        if appointment_ids:
            total_deleted += cleanup_appointments(appointment_ids, db_connection)

        cursor.execute(f"SELECT id FROM review WHERE service_id IN ({placeholders})", service_ids)
        review_ids = [row[0] for row in cursor.fetchall()]
        if review_ids:
            total_deleted += cleanup_reviews(review_ids, db_connection)

        cursor.execute(f"DELETE FROM service WHERE id IN ({placeholders})", service_ids)
        total_deleted += cursor.rowcount

        db_connection.commit()
        cursor.close()

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理服务数据失败: {str(e)}")

    return total_deleted


def cleanup_products(product_ids: List[int], db_connection: Any) -> int:
    """
    清理商品数据

    级联删除关联数据：
    - 订单项数据
    - 购物车数据

    Args:
        product_ids: 商品ID列表
        db_connection: 数据库连接对象

    Returns:
        int: 删除的记录数
    """
    if not product_ids:
        return 0

    total_deleted = 0

    try:
        cursor = db_connection.cursor()

        placeholders = ",".join(["%s"] * len(product_ids))

        cursor.execute(f"DELETE FROM product_order_item WHERE product_id IN ({placeholders})", product_ids)
        total_deleted += cursor.rowcount

        cursor.execute(f"DELETE FROM cart WHERE product_id IN ({placeholders})", product_ids)
        total_deleted += cursor.rowcount

        cursor.execute(f"DELETE FROM product WHERE id IN ({placeholders})", product_ids)
        total_deleted += cursor.rowcount

        db_connection.commit()
        cursor.close()

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理商品数据失败: {str(e)}")

    return total_deleted


def cleanup_appointments(appointment_ids: List[int], db_connection: Any) -> int:
    """
    清理预约数据

    级联删除关联数据：
    - 评价数据

    Args:
        appointment_ids: 预约ID列表
        db_connection: 数据库连接对象

    Returns:
        int: 删除的记录数
    """
    if not appointment_ids:
        return 0

    total_deleted = 0

    try:
        cursor = db_connection.cursor()

        placeholders = ",".join(["%s"] * len(appointment_ids))

        cursor.execute(f"DELETE FROM review WHERE appointment_id IN ({placeholders})", appointment_ids)
        total_deleted += cursor.rowcount

        cursor.execute(f"DELETE FROM appointment WHERE id IN ({placeholders})", appointment_ids)
        total_deleted += cursor.rowcount

        db_connection.commit()
        cursor.close()

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理预约数据失败: {str(e)}")

    return total_deleted


def cleanup_orders(order_ids: List[int], db_connection: Any) -> int:
    """
    清理订单数据

    级联删除关联数据：
    - 订单项数据

    Args:
        order_ids: 订单ID列表
        db_connection: 数据库连接对象

    Returns:
        int: 删除的记录数
    """
    if not order_ids:
        return 0

    total_deleted = 0

    try:
        cursor = db_connection.cursor()

        placeholders = ",".join(["%s"] * len(order_ids))

        cursor.execute(f"DELETE FROM product_order_item WHERE order_id IN ({placeholders})", order_ids)
        total_deleted += cursor.rowcount

        cursor.execute(f"DELETE FROM product_order WHERE id IN ({placeholders})", order_ids)
        total_deleted += cursor.rowcount

        db_connection.commit()
        cursor.close()

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理订单数据失败: {str(e)}")

    return total_deleted


def cleanup_reviews(review_ids: List[int], db_connection: Any) -> int:
    """
    清理评价数据

    Args:
        review_ids: 评价ID列表
        db_connection: 数据库连接对象

    Returns:
        int: 删除的记录数
    """
    if not review_ids:
        return 0

    try:
        cursor = db_connection.cursor()

        placeholders = ",".join(["%s"] * len(review_ids))

        cursor.execute(f"DELETE FROM review WHERE id IN ({placeholders})", review_ids)
        deleted_count = cursor.rowcount

        db_connection.commit()
        cursor.close()

        return deleted_count

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理评价数据失败: {str(e)}")


def cleanup_pets(pet_ids: List[int], db_connection: Any) -> int:
    """
    清理宠物数据

    Args:
        pet_ids: 宠物ID列表
        db_connection: 数据库连接对象

    Returns:
        int: 删除的记录数
    """
    if not pet_ids:
        return 0

    try:
        cursor = db_connection.cursor()

        placeholders = ",".join(["%s"] * len(pet_ids))

        cursor.execute(f"DELETE FROM pet WHERE id IN ({placeholders})", pet_ids)
        deleted_count = cursor.rowcount

        db_connection.commit()
        cursor.close()

        return deleted_count

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理宠物数据失败: {str(e)}")


def cleanup_addresses(address_ids: List[int], db_connection: Any) -> int:
    """
    清理地址数据

    Args:
        address_ids: 地址ID列表
        db_connection: 数据库连接对象

    Returns:
        int: 删除的记录数
    """
    if not address_ids:
        return 0

    try:
        cursor = db_connection.cursor()

        placeholders = ",".join(["%s"] * len(address_ids))

        cursor.execute(f"DELETE FROM address WHERE id IN ({placeholders})", address_ids)
        deleted_count = cursor.rowcount

        db_connection.commit()
        cursor.close()

        return deleted_count

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理地址数据失败: {str(e)}")


def cleanup_all(db_connection: Any, confirm: bool = False) -> int:
    """
    清理所有测试数据

    警告：此操作将删除所有数据，仅在测试环境中使用！

    Args:
        db_connection: 数据库连接对象
        confirm: 确认标志，必须为True才会执行清理

    Returns:
        int: 删除的记录数

    使用示例：
        cleanup_all(db_connection, confirm=True)
    """
    if not confirm:
        raise ValueError("必须设置 confirm=True 才能执行清理所有数据的操作")

    total_deleted = 0

    try:
        cursor = db_connection.cursor()

        tables = [
            "review",
            "product_order_item",
            "product_order",
            "appointment",
            "cart",
            "favorite",
            "address",
            "pet",
            "product",
            "service",
            "merchant",
            "user",
        ]

        for table in tables:
            cursor.execute(f"DELETE FROM {table}")
            total_deleted += cursor.rowcount

        db_connection.commit()
        cursor.close()

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"清理所有数据失败: {str(e)}")

    return total_deleted


def cleanup_by_condition(table: str, condition: str, db_connection: Any, cascade: bool = False) -> int:
    """
    按条件清理数据

    Args:
        table: 表名
        condition: WHERE条件（不包含WHERE关键字）
        db_connection: 数据库连接对象
        cascade: 是否级联删除关联数据

    Returns:
        int: 删除的记录数

    使用示例：
        cleanup_by_condition('user', "status = 'inactive'", db_connection)
    """
    try:
        cursor = db_connection.cursor()

        if cascade:
            cursor.execute(f"SELECT id FROM {table} WHERE {condition}")
            ids = [row[0] for row in cursor.fetchall()]

            if table == "user" and ids:
                return cleanup_users(ids, db_connection)
            elif table == "merchant" and ids:
                return cleanup_merchants(ids, db_connection)
            elif table == "service" and ids:
                return cleanup_services(ids, db_connection)
            elif table == "product" and ids:
                return cleanup_products(ids, db_connection)
            elif table == "appointment" and ids:
                return cleanup_appointments(ids, db_connection)
            elif table == "product_order" and ids:
                return cleanup_orders(ids, db_connection)
            elif table == "review" and ids:
                return cleanup_reviews(ids, db_connection)

        cursor.execute(f"DELETE FROM {table} WHERE {condition}")
        deleted_count = cursor.rowcount

        db_connection.commit()
        cursor.close()

        return deleted_count

    except Exception as e:
        if hasattr(db_connection, "rollback"):
            db_connection.rollback()
        raise RuntimeError(f"按条件清理数据失败: {str(e)}")
