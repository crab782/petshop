import { ref, reactive, type Ref, type UnwrapNestedRefs } from 'vue'

interface UseFormReturn<T> {
  formData: UnwrapNestedRefs<T>
  errors: Ref<Record<string, string>>
  isSubmitting: Ref<boolean>
  isValid: Ref<boolean>
  setFieldValue: (field: keyof T, value: any) => void
  setFieldError: (field: keyof T, error: string) => void
  clearErrors: () => void
  validate: (rules: Record<keyof T, (value: any) => string | null>) => boolean
  handleSubmit: (submitFn: (data: T) => Promise<void>) => Promise<void>
  reset: (initialData?: Partial<T>) => void
}

export function useForm<T extends Record<string, any>>(initialData: T): UseFormReturn<T> {
  const formData = reactive<T>({ ...initialData }) as UnwrapNestedRefs<T>
  const errors = ref<Record<string, string>>({})
  const isSubmitting = ref<boolean>(false)
  const isValid = ref<boolean>(true)

  const setFieldValue = (field: keyof T, value: any): void => {
    ;(formData as T)[field] = value
    if (errors.value[field as string]) {
      const newErrors = { ...errors.value }
      delete newErrors[field as string]
      errors.value = newErrors
    }
  }

  const setFieldError = (field: keyof T, error: string): void => {
    errors.value = {
      ...errors.value,
      [field]: error
    }
    isValid.value = false
  }

  const clearErrors = (): void => {
    errors.value = {}
    isValid.value = true
  }

  const validate = (rules: Record<keyof T, (value: any) => string | null>): boolean => {
    clearErrors()
    let valid = true

    for (const field in rules) {
      const rule = rules[field]
      const value = (formData as T)[field]
      const error = rule(value)

      if (error) {
        errors.value[field as string] = error
        valid = false
      }
    }

    isValid.value = valid
    return valid
  }

  const handleSubmit = async (submitFn: (data: T) => Promise<void>): Promise<void> => {
    if (isSubmitting.value) return

    isSubmitting.value = true

    try {
      const data = { ...formData } as T
      await submitFn(data)
    } finally {
      isSubmitting.value = false
    }
  }

  const reset = (newInitialData?: Partial<T>): void => {
    const data = newInitialData ? { ...initialData, ...newInitialData } : initialData
    Object.assign(formData, data)
    clearErrors()
    isSubmitting.value = false
  }

  return {
    formData,
    errors,
    isSubmitting,
    isValid,
    setFieldValue,
    setFieldError,
    clearErrors,
    validate,
    handleSubmit,
    reset
  }
}
