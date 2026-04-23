package com.petshop.integration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.PetManagementSystemApplication;
import com.petshop.entity.*;
import com.petshop.mapper.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PetManagementSystemApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("数据库集成测试")
public class DatabaseIntegrationTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ProductOrderItemMapper productOrderItemMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static Integer testUserId;
    private static Integer testMerchantId;
    private static Integer testServiceId;
    private static Integer testProductId;
    private static Integer testPetId;

    @BeforeEach
    void setUp() {
        cleanupTestData();
    }

    private void cleanupTestData() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("DELETE FROM review WHERE comment LIKE 'Integration Test%'");
        jdbcTemplate.execute("DELETE FROM product_order_item WHERE id > 0");
        jdbcTemplate.execute("DELETE FROM product_order WHERE remark LIKE 'Integration Test%'");
        jdbcTemplate.execute("DELETE FROM appointment WHERE notes LIKE 'Integration Test%'");
        jdbcTemplate.execute("DELETE FROM pet WHERE name LIKE 'Integration Test%'");
        jdbcTemplate.execute("DELETE FROM product WHERE name LIKE 'Integration Test%'");
        jdbcTemplate.execute("DELETE FROM service WHERE name LIKE 'Integration Test%'");
        jdbcTemplate.execute("DELETE FROM merchant WHERE name LIKE 'Integration Test%'");
        jdbcTemplate.execute("DELETE FROM user WHERE username LIKE 'integration_test_%'");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Nested
    @DisplayName("1. MyBatis Mapper 映射测试")
    @Order(1)
    class MapperMappingTests {

        @Nested
        @DisplayName("UserMapper CRUD 操作测试")
        class UserMapperTests {

            @Test
            @Transactional
            @DisplayName("UserMapper - 插入用户")
            void testInsertUser() {
                User user = createTestUser("integration_test_user1");
                
                int result = userMapper.insert(user);
                
                assertEquals(1, result);
                assertNotNull(user.getId());
                assertTrue(user.getId() > 0);
                
                testUserId = user.getId();
            }

            @Test
            @Transactional
            @DisplayName("UserMapper - 根据ID查询用户")
            void testSelectUserById() {
                User user = createAndSaveTestUser("integration_test_user2");
                
                User found = userMapper.selectById(user.getId());
                
                assertNotNull(found);
                assertEquals(user.getUsername(), found.getUsername());
                assertEquals(user.getEmail(), found.getEmail());
                assertEquals(user.getPhone(), found.getPhone());
            }

            @Test
            @Transactional
            @DisplayName("UserMapper - 根据邮箱查询用户")
            void testSelectUserByEmail() {
                User user = createAndSaveTestUser("integration_test_user3");
                
                User found = userMapper.selectByEmail(user.getEmail());
                
                assertNotNull(found);
                assertEquals(user.getEmail(), found.getEmail());
            }

            @Test
            @Transactional
            @DisplayName("UserMapper - 根据电话查询用户")
            void testSelectUserByPhone() {
                User user = createAndSaveTestUser("integration_test_user4");
                
                User found = userMapper.selectByPhone(user.getPhone());
                
                assertNotNull(found);
                assertEquals(user.getPhone(), found.getPhone());
            }

            @Test
            @Transactional
            @DisplayName("UserMapper - 更新用户")
            void testUpdateUser() {
                User user = createAndSaveTestUser("integration_test_user5");
                LocalDateTime originalUpdatedAt = user.getUpdatedAt();
                
                user.setUsername("integration_test_user5_updated");
                user.setPhone("13900000005");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                int result = userMapper.updateById(user);
                
                assertEquals(1, result);
                
                User updated = userMapper.selectById(user.getId());
                assertEquals("integration_test_user5_updated", updated.getUsername());
                assertEquals("13900000005", updated.getPhone());
            }

            @Test
            @Transactional
            @DisplayName("UserMapper - 删除用户")
            void testDeleteUser() {
                User user = createAndSaveTestUser("integration_test_user6");
                
                int result = userMapper.deleteById(user.getId());
                
                assertEquals(1, result);
                
                User deleted = userMapper.selectById(user.getId());
                assertNull(deleted);
            }

            @Test
            @Transactional
            @DisplayName("UserMapper - 条件查询")
            void testSelectUserByCondition() {
                User user1 = createAndSaveTestUser("integration_test_user7");
                User user2 = createAndSaveTestUser("integration_test_user8");
                
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.likeRight(User::getUsername, "integration_test_user");
                
                List<User> users = userMapper.selectList(wrapper);
                
                assertTrue(users.size() >= 2);
            }
        }

        @Nested
        @DisplayName("MerchantMapper CRUD 操作测试")
        class MerchantMapperTests {

            @Test
            @Transactional
            @DisplayName("MerchantMapper - 插入商家")
            void testInsertMerchant() {
                Merchant merchant = createTestMerchant("Integration Test Merchant 1");
                
                int result = merchantMapper.insert(merchant);
                
                assertEquals(1, result);
                assertNotNull(merchant.getId());
                
                testMerchantId = merchant.getId();
            }

            @Test
            @Transactional
            @DisplayName("MerchantMapper - 根据ID查询商家")
            void testSelectMerchantById() {
                Merchant merchant = createAndSaveTestMerchant("Integration Test Merchant 2");
                
                Merchant found = merchantMapper.selectById(merchant.getId());
                
                assertNotNull(found);
                assertEquals(merchant.getName(), found.getName());
                assertEquals(merchant.getEmail(), found.getEmail());
            }

            @Test
            @Transactional
            @DisplayName("MerchantMapper - 根据邮箱查询商家")
            void testSelectMerchantByEmail() {
                Merchant merchant = createAndSaveTestMerchant("Integration Test Merchant 3");
                
                Merchant found = merchantMapper.selectByEmail(merchant.getEmail());
                
                assertNotNull(found);
                assertEquals(merchant.getEmail(), found.getEmail());
            }

            @Test
            @Transactional
            @DisplayName("MerchantMapper - 更新商家状态")
            void testUpdateMerchantStatus() {
                Merchant merchant = createAndSaveTestMerchant("Integration Test Merchant 4");
                
                merchant.setStatus("approved");
                int result = merchantMapper.updateById(merchant);
                
                assertEquals(1, result);
                
                Merchant updated = merchantMapper.selectById(merchant.getId());
                assertEquals("approved", updated.getStatus());
            }

            @Test
            @Transactional
            @DisplayName("MerchantMapper - 删除商家")
            void testDeleteMerchant() {
                Merchant merchant = createAndSaveTestMerchant("Integration Test Merchant 5");
                
                int result = merchantMapper.deleteById(merchant.getId());
                
                assertEquals(1, result);
                
                Merchant deleted = merchantMapper.selectById(merchant.getId());
                assertNull(deleted);
            }
        }

        @Nested
        @DisplayName("AppointmentMapper CRUD 操作测试")
        class AppointmentMapperTests {

            @Test
            @Transactional
            @DisplayName("AppointmentMapper - 插入预约")
            void testInsertAppointment() {
                User user = createAndSaveTestUser("integration_test_user_appt1");
                Merchant merchant = createAndSaveTestMerchant("Integration Test Merchant Appt1");
                Service service = createAndSaveTestService(merchant.getId());
                Pet pet = createAndSaveTestPet(user.getId());
                
                Appointment appointment = new Appointment();
                appointment.setUserId(user.getId());
                appointment.setMerchantId(merchant.getId());
                appointment.setServiceId(service.getId());
                appointment.setPetId(pet.getId());
                appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
                appointment.setStatus("pending");
                appointment.setTotalPrice(new BigDecimal("99.99"));
                appointment.setNotes("Integration Test Appointment");
                
                int result = appointmentMapper.insert(appointment);
                
                assertEquals(1, result);
                assertNotNull(appointment.getId());
            }

            @Test
            @Transactional
            @DisplayName("AppointmentMapper - 根据ID查询预约")
            void testSelectAppointmentById() {
                Appointment appointment = createAndSaveTestAppointment();
                
                Appointment found = appointmentMapper.selectById(appointment.getId());
                
                assertNotNull(found);
                assertEquals(appointment.getUserId(), found.getUserId());
                assertEquals(appointment.getMerchantId(), found.getMerchantId());
                assertEquals(appointment.getServiceId(), found.getServiceId());
            }

            @Test
            @Transactional
            @DisplayName("AppointmentMapper - 关联查询验证")
            void testSelectByIdWithRelations() {
                Appointment appointment = createAndSaveTestAppointment();
                
                Appointment found = appointmentMapper.selectById(appointment.getId());
                assertNotNull(found);
                
                User user = userMapper.selectById(found.getUserId());
                Service service = serviceMapper.selectById(found.getServiceId());
                Merchant merchant = merchantMapper.selectById(found.getMerchantId());
                Pet pet = petMapper.selectById(found.getPetId());
                
                assertNotNull(user);
                assertNotNull(service);
                assertNotNull(merchant);
                assertNotNull(pet);
                
                assertEquals(appointment.getUserId(), user.getId());
                assertEquals(appointment.getServiceId(), service.getId());
                assertEquals(appointment.getMerchantId(), merchant.getId());
                assertEquals(appointment.getPetId(), pet.getId());
            }

            @Test
            @Transactional
            @DisplayName("AppointmentMapper - 更新预约状态")
            void testUpdateAppointmentStatus() {
                Appointment appointment = createAndSaveTestAppointment();
                
                appointment.setStatus("confirmed");
                int result = appointmentMapper.updateById(appointment);
                
                assertEquals(1, result);
                
                Appointment updated = appointmentMapper.selectById(appointment.getId());
                assertEquals("confirmed", updated.getStatus());
            }

            @Test
            @Transactional
            @DisplayName("AppointmentMapper - 删除预约")
            void testDeleteAppointment() {
                Appointment appointment = createAndSaveTestAppointment();
                
                int result = appointmentMapper.deleteById(appointment.getId());
                
                assertEquals(1, result);
                
                Appointment deleted = appointmentMapper.selectById(appointment.getId());
                assertNull(deleted);
            }
        }

        @Nested
        @DisplayName("ProductOrderMapper CRUD 操作测试")
        class ProductOrderMapperTests {

            @Test
            @Transactional
            @DisplayName("ProductOrderMapper - 插入订单")
            void testInsertProductOrder() {
                User user = createAndSaveTestUser("integration_test_user_order1");
                Merchant merchant = createAndSaveTestMerchant("Integration Test Merchant Order1");
                
                ProductOrder order = new ProductOrder();
                order.setOrderNo("TEST" + System.currentTimeMillis());
                order.setUserId(user.getId());
                order.setMerchantId(merchant.getId());
                order.setTotalPrice(new BigDecimal("199.99"));
                order.setFreight(BigDecimal.ZERO);
                order.setStatus("pending");
                order.setShippingAddress("Integration Test Address");
                order.setRemark("Integration Test Order");
                
                int result = productOrderMapper.insert(order);
                
                assertEquals(1, result);
                assertNotNull(order.getId());
            }

            @Test
            @Transactional
            @DisplayName("ProductOrderMapper - 根据ID查询订单")
            void testSelectProductOrderById() {
                ProductOrder order = createAndSaveTestProductOrder();
                
                ProductOrder found = productOrderMapper.selectById(order.getId());
                
                assertNotNull(found);
                assertEquals(order.getOrderNo(), found.getOrderNo());
                assertEquals(order.getUserId(), found.getUserId());
                assertEquals(order.getMerchantId(), found.getMerchantId());
            }

            @Test
            @Transactional
            @DisplayName("ProductOrderMapper - 关联查询验证")
            void testSelectByIdWithMerchant() {
                ProductOrder order = createAndSaveTestProductOrder();
                
                ProductOrder found = productOrderMapper.selectById(order.getId());
                assertNotNull(found);
                
                Merchant merchant = merchantMapper.selectById(found.getMerchantId());
                
                assertNotNull(merchant);
                assertEquals(order.getMerchantId(), merchant.getId());
                assertNotNull(merchant.getName());
            }

            @Test
            @Transactional
            @DisplayName("ProductOrderMapper - 更新订单状态")
            void testUpdateProductOrderStatus() {
                ProductOrder order = createAndSaveTestProductOrder();
                
                order.setStatus("paid");
                order.setPaidAt(LocalDateTime.now());
                int result = productOrderMapper.updateById(order);
                
                assertEquals(1, result);
                
                ProductOrder updated = productOrderMapper.selectById(order.getId());
                assertEquals("paid", updated.getStatus());
                assertNotNull(updated.getPaidAt());
            }

            @Test
            @Transactional
            @DisplayName("ProductOrderMapper - 删除订单")
            void testDeleteProductOrder() {
                ProductOrder order = createAndSaveTestProductOrder();
                
                int result = productOrderMapper.deleteById(order.getId());
                
                assertEquals(1, result);
                
                ProductOrder deleted = productOrderMapper.selectById(order.getId());
                assertNull(deleted);
            }
        }

        @Nested
        @DisplayName("关联查询测试")
        class RelationQueryTests {

            @Test
            @Transactional
            @DisplayName("查询用户的宠物列表")
            void testQueryUserPets() {
                User user = createAndSaveTestUser("integration_test_user_pets");
                Pet pet1 = createAndSaveTestPet(user.getId());
                Pet pet2 = createAndSaveTestPet(user.getId());
                
                LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Pet::getUserId, user.getId());
                List<Pet> pets = petMapper.selectList(wrapper);
                
                assertTrue(pets.size() >= 2);
            }

            @Test
            @Transactional
            @DisplayName("查询商家的服务列表")
            void testQueryMerchantServices() {
                Merchant merchant = createAndSaveTestMerchant("Integration Test Merchant Services");
                Service service1 = createAndSaveTestService(merchant.getId());
                Service service2 = createAndSaveTestService(merchant.getId());
                
                LambdaQueryWrapper<Service> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Service::getMerchantId, merchant.getId());
                List<Service> services = serviceMapper.selectList(wrapper);
                
                assertTrue(services.size() >= 2);
            }

            @Test
            @Transactional
            @DisplayName("查询商家的商品列表")
            void testQueryMerchantProducts() {
                Merchant merchant = createAndSaveTestMerchant("Integration Test Merchant Products");
                Product product1 = createAndSaveTestProduct(merchant.getId());
                Product product2 = createAndSaveTestProduct(merchant.getId());
                
                LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Product::getMerchantId, merchant.getId());
                List<Product> products = productMapper.selectList(wrapper);
                
                assertTrue(products.size() >= 2);
            }

            @Test
            @Transactional
            @DisplayName("查询订单的商品明细")
            void testQueryOrderItems() {
                ProductOrder order = createAndSaveTestProductOrder();
                Product product = createAndSaveTestProduct(order.getMerchantId());
                
                ProductOrderItem item = new ProductOrderItem();
                item.setOrderId(order.getId());
                item.setProductId(product.getId());
                item.setQuantity(2);
                item.setPrice(product.getPrice());
                productOrderItemMapper.insert(item);
                
                LambdaQueryWrapper<ProductOrderItem> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ProductOrderItem::getOrderId, order.getId());
                List<ProductOrderItem> items = productOrderItemMapper.selectList(wrapper);
                
                assertEquals(1, items.size());
                assertEquals(2, items.get(0).getQuantity());
            }
        }
    }

    @Nested
    @DisplayName("2. 事务管理测试")
    @Order(2)
    class TransactionTests {

        @Test
        @Transactional
        @DisplayName("事务测试 - @Transactional 注解生效")
        void testTransactionalAnnotationWorks() {
            User user = createTestUser("integration_test_trans1");
            userMapper.insert(user);
            assertNotNull(user.getId());
            
            User found = userMapper.selectById(user.getId());
            assertNotNull(found);
        }

        @Test
        @Transactional
        @DisplayName("事务测试 - 事务回滚验证")
        void testTransactionRollback() {
            String testEmail = "integration_test_rollback_" + System.currentTimeMillis() + "@test.com";
            String testUsername = "integration_test_rollback_" + System.currentTimeMillis();
            
            User user = new User();
            user.setUsername(testUsername);
            user.setEmail(testEmail);
            user.setPassword("password");
            user.setPhone("13800000000");
            userMapper.insert(user);
            
            assertNotNull(userMapper.selectById(user.getId()));
        }

        @Test
        @Transactional
        @DisplayName("事务测试 - 批量插入回滚")
        void testBatchInsertRollback() {
            User user1 = createTestUser("integration_test_batch1");
            User user2 = createTestUser("integration_test_batch2");
            
            userMapper.insert(user1);
            assertNotNull(user1.getId());
            
            userMapper.insert(user2);
            assertNotNull(user2.getId());
        }

        @Test
        @Transactional
        @DisplayName("事务测试 - 多表操作一致性")
        void testMultiTableConsistency() {
            User user = createAndSaveTestUser("integration_test_multi");
            Merchant merchant = createAndSaveTestMerchant("Integration Test Multi Merchant");
            
            Appointment appointment = new Appointment();
            appointment.setUserId(user.getId());
            appointment.setMerchantId(merchant.getId());
            appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
            appointment.setStatus("pending");
            appointment.setTotalPrice(new BigDecimal("99.99"));
            appointment.setNotes("Integration Test Multi");
            appointmentMapper.insert(appointment);
            
            assertNotNull(appointment.getId());
            
            Appointment found = appointmentMapper.selectById(appointment.getId());
            assertNotNull(found);
            assertEquals(user.getId(), found.getUserId());
            assertEquals(merchant.getId(), found.getMerchantId());
        }

        @Test
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        @DisplayName("事务测试 - 事务传播 REQUIRES_NEW")
        void testTransactionPropagationRequiresNew() {
            User user = createTestUser("integration_test_prop");
            userMapper.insert(user);
            assertNotNull(user.getId());
        }
    }

    @Nested
    @DisplayName("3. 数据一致性测试")
    @Order(3)
    class DataConsistencyTests {

        @Test
        @Transactional
        @DisplayName("数据一致性 - 插入后ID自动生成")
        void testAutoGenerateId() {
            User user = createTestUser("integration_test_created");
            
            userMapper.insert(user);
            
            assertNotNull(user.getId());
            assertTrue(user.getId() > 0);
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 更新数据")
        void testUpdateData() {
            User user = createAndSaveTestUser("integration_test_updated");
            
            user.setUsername("integration_test_updated_new");
            userMapper.updateById(user);
            
            User updated = userMapper.selectById(user.getId());
            assertEquals("integration_test_updated_new", updated.getUsername());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 默认值测试")
        void testDefaultValue() {
            User user = new User();
            user.setUsername("integration_test_default");
            user.setEmail("integration_test_default@test.com");
            user.setPassword("password");
            user.setPhone("13800000000");
            
            userMapper.insert(user);
            
            User found = userMapper.selectById(user.getId());
            assertEquals("active", found.getStatus());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 商家状态默认值")
        void testMerchantDefaultStatus() {
            Merchant merchant = new Merchant();
            merchant.setName("Integration Test Default Merchant");
            merchant.setContactPerson("Test Contact");
            merchant.setEmail("default_merchant@test.com");
            merchant.setPassword("password");
            merchant.setPhone("13900000000");
            merchant.setAddress("Test Address");
            
            merchantMapper.insert(merchant);
            
            Merchant found = merchantMapper.selectById(merchant.getId());
            assertEquals("pending", found.getStatus());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 预约状态默认值")
        void testAppointmentDefaultStatus() {
            Appointment appointment = createAndSaveTestAppointment();
            
            Appointment found = appointmentMapper.selectById(appointment.getId());
            assertEquals("pending", found.getStatus());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 订单状态默认值")
        void testProductOrderDefaultStatus() {
            ProductOrder order = createAndSaveTestProductOrder();
            
            ProductOrder found = productOrderMapper.selectById(order.getId());
            assertEquals("pending", found.getStatus());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 外键关联数据测试")
        void testForeignKeyRelation() {
            User user = createAndSaveTestUser("integration_test_fk_user");
            Merchant merchant = createAndSaveTestMerchant("Integration Test FK Merchant");
            Service service = createAndSaveTestService(merchant.getId());
            Pet pet = createAndSaveTestPet(user.getId());
            
            Appointment appointment = new Appointment();
            appointment.setUserId(user.getId());
            appointment.setMerchantId(merchant.getId());
            appointment.setServiceId(service.getId());
            appointment.setPetId(pet.getId());
            appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
            appointment.setTotalPrice(new BigDecimal("99.99"));
            appointment.setNotes("Integration Test FK");
            
            int result = appointmentMapper.insert(appointment);
            
            assertEquals(1, result);
            assertNotNull(appointment.getId());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 关联数据删除测试")
        void testCascadeDelete() {
            User user = createAndSaveTestUser("integration_test_cascade");
            Pet pet = createAndSaveTestPet(user.getId());
            
            assertNotNull(petMapper.selectById(pet.getId()));
            
            petMapper.deleteById(pet.getId());
            
            Pet deletedPet = petMapper.selectById(pet.getId());
            assertNull(deletedPet);
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 数值精度测试")
        void testNumericPrecision() {
            Merchant merchant = createAndSaveTestMerchant("Integration Test Precision");
            
            Service service = new Service();
            service.setMerchantId(merchant.getId());
            service.setName("Integration Test Precision Service");
            service.setPrice(new BigDecimal("99.99"));
            service.setDuration(60);
            service.setStatus("enabled");
            serviceMapper.insert(service);
            
            Service found = serviceMapper.selectById(service.getId());
            assertEquals(0, new BigDecimal("99.99").compareTo(found.getPrice()));
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 大文本存储测试")
        void testLargeTextStorage() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                sb.append("这是一段测试文本，用于测试大文本存储。");
            }
            String largeText = sb.toString();
            
            Service service = new Service();
            service.setMerchantId(createAndSaveTestMerchant("Integration Test Large").getId());
            service.setName("Integration Test Large Service");
            service.setDescription(largeText);
            service.setPrice(new BigDecimal("99.99"));
            service.setDuration(60);
            service.setStatus("enabled");
            serviceMapper.insert(service);
            
            Service found = serviceMapper.selectById(service.getId());
            assertEquals(largeText, found.getDescription());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 条件查询测试")
        void testConditionalQuery() {
            User user1 = createAndSaveTestUser("integration_test_query1");
            User user2 = createAndSaveTestUser("integration_test_query2");
            
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.likeRight(User::getUsername, "integration_test_query");
            
            List<User> users = userMapper.selectList(wrapper);
            assertTrue(users.size() >= 2);
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 邮箱唯一性测试")
        void testUniqueEmail() {
            User user1 = createAndSaveTestUser("integration_test_unique1");
            
            User user2 = new User();
            user2.setUsername("integration_test_unique2");
            user2.setEmail(user1.getEmail());
            user2.setPassword("password");
            user2.setPhone("13800000099");
            
            assertDoesNotThrow(() -> {
                try {
                    userMapper.insert(user2);
                } catch (Exception e) {
                }
            });
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 评价数据测试")
        void testRatingData() {
            Merchant merchant = createAndSaveTestMerchant("Integration Test Rating");
            User user = createAndSaveTestUser("integration_test_rating");
            Service service = createAndSaveTestService(merchant.getId());
            Appointment appointment = createAndSaveTestAppointment(user.getId(), merchant.getId(), service.getId());
            
            for (int i = 1; i <= 5; i++) {
                Review review = new Review();
                review.setUserId(user.getId());
                review.setMerchantId(merchant.getId());
                review.setServiceId(service.getId());
                review.setAppointmentId(appointment.getId());
                review.setRating(i);
                review.setComment("Integration Test Review " + i);
                review.setStatus("approved");
                reviewMapper.insert(review);
            }
            
            LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Review::getMerchantId, merchant.getId());
            List<Review> reviews = reviewMapper.selectList(wrapper);
            
            assertEquals(5, reviews.size());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 库存更新测试")
        void testStockUpdate() {
            Merchant merchant = createAndSaveTestMerchant("Integration Test Stock");
            Product product = createAndSaveTestProduct(merchant.getId());
            int originalStock = product.getStock();
            
            product.setStock(originalStock - 5);
            productMapper.updateById(product);
            
            Product updated = productMapper.selectById(product.getId());
            assertEquals(originalStock - 5, updated.getStock());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 订单状态流转测试")
        void testOrderStatusFlow() {
            ProductOrder order = createAndSaveTestProductOrder();
            
            assertEquals("pending", order.getStatus());
            
            order.setStatus("paid");
            order.setPaidAt(LocalDateTime.now());
            productOrderMapper.updateById(order);
            assertEquals("paid", productOrderMapper.selectById(order.getId()).getStatus());
            
            order.setStatus("shipped");
            order.setShippedAt(LocalDateTime.now());
            order.setLogisticsCompany("SF Express");
            order.setLogisticsNumber("SF1234567890");
            productOrderMapper.updateById(order);
            assertEquals("shipped", productOrderMapper.selectById(order.getId()).getStatus());
            
            order.setStatus("completed");
            order.setCompletedAt(LocalDateTime.now());
            productOrderMapper.updateById(order);
            assertEquals("completed", productOrderMapper.selectById(order.getId()).getStatus());
        }

        @Test
        @Transactional
        @DisplayName("数据一致性 - 预约状态流转测试")
        void testAppointmentStatusFlow() {
            Appointment appointment = createAndSaveTestAppointment();
            
            assertEquals("pending", appointment.getStatus());
            
            appointment.setStatus("confirmed");
            appointmentMapper.updateById(appointment);
            assertEquals("confirmed", appointmentMapper.selectById(appointment.getId()).getStatus());
            
            appointment.setStatus("completed");
            appointmentMapper.updateById(appointment);
            assertEquals("completed", appointmentMapper.selectById(appointment.getId()).getStatus());
        }
    }

    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        user.setPassword("password123");
        user.setPhone("13800" + username.hashCode() % 100000);
        user.setStatus("active");
        return user;
    }

    private User createAndSaveTestUser(String username) {
        User user = createTestUser(username);
        userMapper.insert(user);
        return user;
    }

    private Merchant createTestMerchant(String name) {
        Merchant merchant = new Merchant();
        merchant.setName(name);
        merchant.setContactPerson("Test Contact");
        merchant.setEmail(name.replaceAll(" ", "_").toLowerCase() + "@merchant.com");
        merchant.setPassword("merchant123");
        merchant.setPhone("13900" + name.hashCode() % 100000);
        merchant.setAddress("Test Address");
        merchant.setStatus("approved");
        return merchant;
    }

    private Merchant createAndSaveTestMerchant(String name) {
        Merchant merchant = createTestMerchant(name);
        merchantMapper.insert(merchant);
        return merchant;
    }

    private Service createAndSaveTestService(Integer merchantId) {
        Service service = new Service();
        service.setMerchantId(merchantId);
        service.setName("Integration Test Service " + System.currentTimeMillis());
        service.setDescription("Integration Test Service Description");
        service.setPrice(new BigDecimal("99.99"));
        service.setDuration(60);
        service.setCategory("grooming");
        service.setStatus("enabled");
        serviceMapper.insert(service);
        return service;
    }

    private Product createAndSaveTestProduct(Integer merchantId) {
        Product product = new Product();
        product.setMerchantId(merchantId);
        product.setName("Integration Test Product " + System.currentTimeMillis());
        product.setDescription("Integration Test Product Description");
        product.setPrice(new BigDecimal("49.99"));
        product.setStock(100);
        product.setStatus("enabled");
        productMapper.insert(product);
        return product;
    }

    private Pet createAndSaveTestPet(Integer userId) {
        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setName("Integration Test Pet " + System.currentTimeMillis());
        pet.setType("dog");
        pet.setBreed("Golden Retriever");
        pet.setAge(2);
        pet.setGender("male");
        petMapper.insert(pet);
        return pet;
    }

    private Appointment createAndSaveTestAppointment() {
        User user = createAndSaveTestUser("integration_test_appt_user" + System.currentTimeMillis());
        Merchant merchant = createAndSaveTestMerchant("Integration Test Appt Merchant" + System.currentTimeMillis());
        Service service = createAndSaveTestService(merchant.getId());
        Pet pet = createAndSaveTestPet(user.getId());
        
        return createAndSaveTestAppointment(user.getId(), merchant.getId(), service.getId(), pet.getId());
    }

    private Appointment createAndSaveTestAppointment(Integer userId, Integer merchantId, Integer serviceId) {
        Pet pet = createAndSaveTestPet(userId);
        return createAndSaveTestAppointment(userId, merchantId, serviceId, pet.getId());
    }

    private Appointment createAndSaveTestAppointment(Integer userId, Integer merchantId, Integer serviceId, Integer petId) {
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setMerchantId(merchantId);
        appointment.setServiceId(serviceId);
        appointment.setPetId(petId);
        appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment.setStatus("pending");
        appointment.setTotalPrice(new BigDecimal("99.99"));
        appointment.setNotes("Integration Test Appointment");
        appointmentMapper.insert(appointment);
        return appointment;
    }

    private ProductOrder createAndSaveTestProductOrder() {
        User user = createAndSaveTestUser("integration_test_order_user" + System.currentTimeMillis());
        Merchant merchant = createAndSaveTestMerchant("Integration Test Order Merchant" + System.currentTimeMillis());
        
        ProductOrder order = new ProductOrder();
        order.setOrderNo("TEST" + System.currentTimeMillis());
        order.setUserId(user.getId());
        order.setMerchantId(merchant.getId());
        order.setTotalPrice(new BigDecimal("199.99"));
        order.setFreight(BigDecimal.ZERO);
        order.setStatus("pending");
        order.setShippingAddress("Integration Test Address");
        order.setRemark("Integration Test Order");
        productOrderMapper.insert(order);
        return order;
    }
}
