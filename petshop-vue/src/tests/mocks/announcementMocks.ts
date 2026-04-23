import { vi } from 'vitest'

export interface MockAnnouncement {
  id: number
  title: string
  content: string
  status: 'published' | 'draft'
  publishTime: string
}

export const defaultAnnouncement: MockAnnouncement = {
  id: 1,
  title: '系统公告',
  content: '欢迎使用宠物服务平台，我们致力于为您提供最优质的宠物服务体验。',
  status: 'published',
  publishTime: '2024-04-01T00:00:00.000Z',
}

export const createAnnouncement = (overrides: Partial<MockAnnouncement> = {}): MockAnnouncement => ({
  ...defaultAnnouncement,
  ...overrides,
})

export const createAnnouncementList = (count: number): MockAnnouncement[] =>
  Array.from({ length: count }, (_, i) => createAnnouncement({ id: i + 1, title: `公告${i + 1}` }))

export const mockAnnouncementApi = {
  getAnnouncements: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAnnouncementList(10),
    })
  ),

  getAnnouncementById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAnnouncement({ id }),
    })
  ),

  addAnnouncement: vi.fn((data: { title: string; content: string }) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: createAnnouncement({ id: Date.now(), ...data, publishTime: new Date().toISOString() }),
    })
  ),

  updateAnnouncement: vi.fn((id: number, data: { title: string; content: string }) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAnnouncement({ id, ...data }),
    })
  ),

  deleteAnnouncement: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  publishAnnouncement: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '发布成功',
      data: createAnnouncement({ id, status: 'published' }),
    })
  ),

  unpublishAnnouncement: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '取消发布成功',
      data: createAnnouncement({ id, status: 'draft' }),
    })
  ),
}

export const mockAnnouncementResponses = {
  announcementList: {
    code: 200,
    message: 'success',
    data: [defaultAnnouncement],
  },
  announcementDetail: {
    code: 200,
    message: 'success',
    data: defaultAnnouncement,
  },
}
